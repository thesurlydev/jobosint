package com.jobosint.service

import com.jobosint.model.LinkedInJobSearchRequest
import com.jobosint.model.LinkedInResult
import com.jobosint.utils.getCookiesForHost
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.springframework.stereotype.Service

@Service
class LinkedInService(val browser: Browser) {

    fun searchJobs(jobSearchRequest: LinkedInJobSearchRequest): Set<LinkedInResult> {
        val allResults = mutableSetOf<LinkedInResult>()

        val scrollSleepInterval = 500L
        val scrollIncrement = 350

        val term = jobSearchRequest.term
        val maxResults = jobSearchRequest.maxResults

        val options = LaunchOptions()
//            .setHeadless(false) // Run in headful mode
            .setHeadless(true) // Run in headful mode
//            .setSlowMo(2000.0) // Slow motion delay in milliseconds

        val linkedInCookies = getCookiesForHost( "linkedin.com")

        Playwright.create().use { playwright ->

            val browser = playwright.chromium().launch(options)

            // prevent the Chrome location popup from appearing
            val context = browser
                .newContext(
                    Browser.NewContextOptions()
                        .setPermissions(listOf("geolocation"))
                )

            context.addCookies(linkedInCookies)

            val page: Page = context.newPage()

            // dismiss other popups
            page.onDialog { dialog ->
                println("dialog message: ${dialog.message()}")
                dialog.dismiss()
            }

            /*
                Date Posted Filters:
                 - f_TPR=r86400 (posted in the last 24 hours)
                 - f_TPR=r604800 (posted in the last 7 days)

                Remote filters:
                - f_WT=1 (on-site jobs)
                - f_WT=2 (remote jobs)
                - f_WT=3 (hybrid jobs)
                 */

            page.navigate("https://www.linkedin.com/jobs/search/?f_TPR=r86400&f_WT=2&keywords=${term}")

            var nextPage = 2

            while (true) {
                println("Waiting for page ${nextPage - 1} to load")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)

                val searchResultsPaneSelector = "div.jobs-search-results-list"
                page.focus(searchResultsPaneSelector)

                val searchResultsPane = page.querySelector(searchResultsPaneSelector)
                val searchPaneScrollHeight: Int =
                    page.evaluate("element => element.scrollHeight", searchResultsPane) as Int

                println("searchPaneScrollHeight: $searchPaneScrollHeight")
                var currentScrollHeight = 0
                while (currentScrollHeight <= searchPaneScrollHeight) {

                    // we need to incrementally scroll to get all the results
                    val scrollScript = String.format(
                        "const div = document.querySelector('%s'); div.scrollTop = %s;",
                        searchResultsPaneSelector,
                        currentScrollHeight
                    )
                    page.evaluate(scrollScript).let { scrollResult ->
                        println("scroll result: $scrollResult")
                        page.querySelector(searchResultsPaneSelector).querySelectorAll("a[href*='/jobs/view/']")
                            .let { links ->
                                links?.forEach { link ->
                                    val href = link.getAttribute("href") ?: return@forEach
                                    val text = link.textContent()
                                    val result = LinkedInResult(href, text)
                                    allResults.add(result)
                                    if (allResults.size == maxResults) {
                                        println("max results reached: ${allResults.size}")
                                        return allResults
                                    }
                                }
                            }
                    }
                    println("all results: ${allResults.size}")
                    Thread.sleep(scrollSleepInterval)
                    currentScrollHeight += scrollIncrement
                }

                // page navigation
                val buttonSelector = "button[aria-label='Page $nextPage']"
                val button = page.querySelector(buttonSelector)

                if (button != null) {
                    nextPage += 1
                    button.click()
                } else {
                    println("Button not found")
                    break
                }
            }
            return allResults
        }
    }
}