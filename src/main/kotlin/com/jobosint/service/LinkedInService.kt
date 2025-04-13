package com.jobosint.service

import com.jobosint.client.HttpClientFactory
import com.jobosint.event.BrowserPageUrlCreatedEvent
import com.jobosint.model.*
import com.jobosint.model.browse.BrowserPage
import com.jobosint.model.browse.BrowserPageUrl
import com.jobosint.model.browse.BrowserSession
import com.jobosint.parse.LinkedInParser
import com.jobosint.playwright.CookieService
import com.jobosint.repository.BrowserPageRepository
import com.jobosint.repository.BrowserPageUrlRepository
import com.jobosint.repository.BrowserSessionRepository
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.util.function.Consumer

@Service
class LinkedInService(
    val browser: Browser,
    val linkedInParser: LinkedInParser,
    val jobService: JobService,
    val scrapeService: ScrapeService,
    val cookieService: CookieService,
    val browserSessionRepository: BrowserSessionRepository,
    val browserPageRepository: BrowserPageRepository,
    val browserPageUrlRepository: BrowserPageUrlRepository,
    val httpClientFactory: HttpClientFactory,
    val applicationEventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(LinkedInService::class.java)


    fun getQueryIdFromJobId(jobId: String): String {
        val uri: URI?
        try {
            uri =
                URI("https://www.linkedin.com/jobs/search/?currentJobId=${jobId}&f_WT=2&keywords=java&origin=JOBS_HOME_SEARCH_BUTTON&refresh=true")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

        val linkedInCookieString = cookieService.loadLinkedInRawCookieString()

        val request = HttpRequest.newBuilder(uri)
            .header("Accept", "*/*")
            .header("Cookie", linkedInCookieString)
            .GET().build()

        val response: HttpResponse<String>
        try {
            response = httpClientFactory.client.send(request, BodyHandlers.ofString());
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        if (response.statusCode() != 200) {
            throw RuntimeException("Failed to get job detail: ${response.statusCode()}")
        }

        var queryId = ""
        response.body()
            .let { body ->
                val doc = Jsoup.parse(body)
                val codeTags = doc.select("code")
                codeTags.stream()
                    .filter { ct -> ct.attr("id").startsWith("datalet") && ct.text().contains("jobPostingUrn") }
                    .findFirst()
                    .map { fct ->
                        val startIdx = fct.text().indexOf("voyagerJobsDashJobPostingDetailSections.") + 40
                        val substr = fct.text().substring(startIdx)
                        queryId = substr.substringBefore("\"")
                    }
            }

        return queryId
    }

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
        val doc = Jsoup.parse(content)
        val figure = doc.select("figure.closed-job")
        return figure.isEmpty()
    }

    fun getJobBoardIdFromUrl(url: String): String {
        val baseUrl: String = removeQueryString(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val jobId = urlParts[5]
        // verify jobId is a number
        if (!jobId.matches("\\d+".toRegex())) {
            throw IllegalArgumentException("Invalid jobId: $jobId")
        }
        return jobId
    }

    fun getJobBoardIdFromPath(path: String): String {
        val baseUrl: String = removeQueryString(path)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val jobId = urlParts[3]
        // verify jobId is a number
        if (!jobId.matches("\\d+".toRegex())) {
            throw IllegalArgumentException("Invalid jobId: $jobId")
        }
        return jobId
    }

    public fun removeQueryString(url: String): String {
        var baseUrl: String = url
        if (baseUrl.contains("?")) {
            baseUrl = url.substring(0, url.indexOf("?"))
        }
        return baseUrl
    }

    // https://www.linkedin.com/company/sleeperhq/about/ -> sleeperhq
    fun getCompanyTokenFromUrl(url: String): String {
        val baseUrl = removeQueryString(url)

        // get the board token and job id from the page url
        val urlParts = baseUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val token = urlParts[4]
        return token
    }

    // https://www.linkedin.com/company/arcadiahq/about/
    fun scrapeCompany(companyTag: String, cookies: List<Map<String, String>>?): Company? {

        val url = String.format("https://www.linkedin.com/company/%s/about/", companyTag)

        log.info("Scraping company: $url")

        val scrapeResponse: ScrapeResponse = scrapeService.scrapeHtml(url, cookies)

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
           // .setHeadless(false) // Run in headful mode
            .setSlowMo(2000.0) // Slow motion delay in milliseconds

        Playwright.create().use { playwright ->

            val browser = playwright.chromium().launch(options)

            // prevent the Chrome location popup from appearing
            val context = browser
                .newContext(
                    Browser.NewContextOptions()
                        .setPermissions(listOf("geolocation"))
                )

            val linkedInCookies = cookieService.loadLinkedInPlaywrightCookies()
            context.addCookies(linkedInCookies)

            val page: Page = context.newPage()

            // dismiss other popups
            page.onDialog { dialog ->
                log.info("dialog message: ${dialog.message()}")
                dialog.dismiss()
            }

            /*
                Date Posted Filters:
                 - f_TPR=r3600 (posted in the last hour)
                 - f_TPR=r43200 (posted in the last 12 hours)
                 - f_TPR=r86400 (posted in the last 24 hours)
                 - f_TPR=r604800 (posted in the last 7 days)

                Remote filters:
                - f_WT=1 (on-site jobs)
                - f_WT=2 (remote jobs)
                - f_WT=3 (hybrid jobs)
                 */

            val startPageUrl = "https://www.linkedin.com/jobs/search/?f_TPR=r86400&f_WT=2&keywords=${term}"

            val browserSession = BrowserSession("LinkedInServiceTest", startPageUrl)
            val savedBrowserSession = browserSessionRepository.save(browserSession)
            log.info("Saved browser session: ${savedBrowserSession.id}")

            page.navigate(startPageUrl)

            var nextPage = 2

            while (true) {
                log.info("Waiting for page ${nextPage - 1} to load")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)

                // TODO save page to disk and get path
                val browserPage = BrowserPage(savedBrowserSession.id(), page.url())
                val savedBrowserPage = browserPageRepository.save(browserPage)
                log.info("Saved browser page: ${savedBrowserPage.id}")

                val searchResultsPaneSelector =
                    "#main > div > div.scaffold-layout__list-detail-inner.scaffold-layout__list-detail-inner--grow > div.scaffold-layout__list > div"
                page.focus(searchResultsPaneSelector)

                val searchResultsPane = page.querySelector(searchResultsPaneSelector)
                val searchPaneScrollHeight: Int =
                    page.evaluate("element => element.scrollHeight", searchResultsPane) as Int

                log.info("searchPaneScrollHeight: $searchPaneScrollHeight")
                var currentScrollHeight = 0
                while (currentScrollHeight <= searchPaneScrollHeight) {

                    // we need to incrementally scroll to get all the results
                    val scrollScript = String.format(
                        "const div = document.querySelector('%s'); div.scrollTop = %s;",
                        searchResultsPaneSelector,
                        currentScrollHeight
                    )
                    page.evaluate(scrollScript).let { scrollResult ->
                        log.info("scroll result: $scrollResult")
                        page.querySelector(searchResultsPaneSelector).querySelectorAll("a[href*='/jobs/view/']")
                            .let { links ->
                                links?.forEach { link ->
                                    val href = link.getAttribute("href") ?: return@forEach
                                    val text = link.getAttribute("aria-label").replace("with verification", "").trim()
                                    val result = LinkedInResult(href, text)
                                    allResults.add(result)
                                    if (allResults.size == maxResults) {
                                        log.info("max results reached: ${allResults.size}")

                                        allResults.stream()
                                            .map { liResult ->
                                                val jobId = getJobBoardIdFromPath(liResult.href)
                                                val url = "https://www.linkedin.com/jobs/view/$jobId"
                                                BrowserPageUrl(
                                                    savedBrowserPage.id,
                                                    url,
                                                    liResult.text
                                                )
                                            }
                                            .forEach { bpu ->
                                                val savedBrowserPageUrl = browserPageUrlRepository.save(bpu)
                                                applicationEventPublisher.publishEvent(
                                                    BrowserPageUrlCreatedEvent(
                                                        this,
                                                        savedBrowserPageUrl
                                                    )
                                                )
                                            }

                                        return allResults
                                    }
                                }
                            }
                    }
                    log.info("all results: ${allResults.size}")
                    Thread.sleep(scrollSleepInterval)
                    currentScrollHeight += scrollIncrement
                }
                log.info("Finished scrolling; looking for paging button for page ${nextPage}")

                // page navigation
                val buttonSelector = "button[aria-label='Page $nextPage']"
                val button = page.querySelector(buttonSelector)

                if (button != null) {
                    log.info("Found paging button for page $nextPage")
                    nextPage += 1
                    button.click()
                } else {
                    log.warn("Button not found")
                    break
                }
            }
            return allResults
        }
    }
}