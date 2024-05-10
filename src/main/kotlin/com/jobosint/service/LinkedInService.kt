package com.jobosint.service

import com.jobosint.model.Company
import com.jobosint.model.CompanyParserResult
import com.jobosint.model.JobPageDetail
import com.jobosint.model.LinkedInJobSearchRequest
import com.jobosint.model.LinkedInResult
import com.jobosint.model.ScrapeResponse
import com.jobosint.parse.LinkedInParser
import com.jobosint.utils.getCookiesForHost
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class LinkedInService(
    val browser: Browser,
    val linkedInParser: LinkedInParser,
    val jobService: JobService,
    val scrapeService: ScrapeService,
) {

    private val log = LoggerFactory.getLogger(LinkedInService::class.java)

    fun updateJobBoardIds() {
        jobService.findAllJobPageDetail("LinkedIn").forEach(Consumer { jobPageDetail: JobPageDetail ->
            println(jobPageDetail)
            val pageUrl = jobPageDetail.pageUrl
            try {
                val jobBoardId = getJobBoardIdFromUrl(pageUrl)
                jobService.updateJobBoardId(jobPageDetail.id, jobBoardId)
            } catch (e: IllegalArgumentException) {
                log.warn("Invalid jobId: $pageUrl")
            }
        })
    }

    fun jobStillAcceptingApplications(jobId: String): Boolean {
        val url = String.format("https://www.linkedin.com/jobs/view/%s", jobId)
        val scrapeResponse: ScrapeResponse = scrapeService.scrapeHtml(url)
        val content = java.lang.String.join(System.lineSeparator(), scrapeResponse.data())
        println(content)
        val doc = Jsoup.parse(content)

        val figure = doc.select("figure.closed-job")
        return figure.isEmpty()
    }

    fun getJobBoardIdFromUrl(url: String): String {
        val baseUrl: String = getBaseUrl(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val jobId = urlParts[5]
        // verify jobId is a number
        if (!jobId.matches("\\d+".toRegex())) {
            throw IllegalArgumentException("Invalid jobId: $jobId")
        }
        return jobId
    }

    private fun getBaseUrl(url: String): String {
        var baseUrl: String = url
        if (baseUrl.contains("?")) {
            baseUrl = url.substring(0, url.indexOf("?"))
        }
        return baseUrl
    }

    // https://www.linkedin.com/company/sleeperhq/about/ -> sleeperhq
    fun getCompanyTokenFromUrl(url: String): String {
        val baseUrl = getBaseUrl(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val token = urlParts[4]
        return token
    }

    // https://www.linkedin.com/company/arcadiahq/about/
    fun scrapeCompany(companyTag: String): Company? {

        val url = String.format("https://www.linkedin.com/company/%s/about/", companyTag)

        log.info("Scraping company: $url")

        val scrapeResponse: ScrapeResponse = scrapeService.scrapeHtml(url)

        log.info("Scrape response: $scrapeResponse")

        if (scrapeResponse.data() == null || scrapeResponse.data().isEmpty()) {
            return null
        }

        val linkedInToken: String = getCompanyTokenFromUrl(url)

        val content = java.lang.String.join(System.lineSeparator(), scrapeResponse.data())
        val companyParserResult: CompanyParserResult = linkedInParser.parseCompanyDescriptionFromString(content)
        val company = Company(
            null,
            companyParserResult.name,
            companyParserResult.websiteUrl,
            null,
            companyParserResult.employeeCount,
            companyParserResult.summary,
            companyParserResult.location,
            linkedInToken,
            null
        )

        return company
    }

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

        val linkedInCookies = getCookiesForHost("linkedin.com")

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
                                    val text = link.textContent().replace("\n", "").trim()
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