package com.jobosint.service

import com.jobosint.client.HttpClientFactory
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
import com.sun.tools.javac.resources.ct
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.*
import java.net.http.HttpResponse.BodyHandlers.*
import java.nio.file.Path
import java.nio.file.StandardOpenOption
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
    val httpClientFactory: HttpClientFactory
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

        val request = HttpRequest.newBuilder(uri)
            .header("Accept", "*/*")
            .header("Cookie", cookie())
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

    /*fun getJobDetail(String url): JobDetail {

    }*/

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
            .setHeadless(false) // Run in headful mode
//            .setSlowMo(2000.0) // Slow motion delay in milliseconds

        Playwright.create().use { playwright ->

            val browser = playwright.chromium().launch(options)

            // prevent the Chrome location popup from appearing
            val context = browser
                .newContext(
                    Browser.NewContextOptions()
                        .setPermissions(listOf("geolocation"))
                )

            val linkedInCookies = cookieService.loadLinkedInCookies()
            context.addCookies(linkedInCookies)

            val page: Page = context.newPage()

            // dismiss other popups
            page.onDialog { dialog ->
                println("dialog message: ${dialog.message()}")
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
            println("Saved browser session: ${savedBrowserSession.id}")

            page.navigate(startPageUrl)

            var nextPage = 2

            while (true) {
                println("Waiting for page ${nextPage - 1} to load")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)

                // TODO save page to disk and get path
                val browserPage = BrowserPage(savedBrowserSession.id(), page.url())
                val savedBrowserPage = browserPageRepository.save(browserPage)
                println("Saved browser page: ${savedBrowserPage.id}")

                val searchResultsPaneSelector =
                    "#main > div > div.scaffold-layout__list-detail-inner.scaffold-layout__list-detail-inner--grow > div.scaffold-layout__list > div"
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
                                    val text = link.getAttribute("aria-label").replace("with verification", "").trim()
                                        ?: return@forEach
                                    val result = LinkedInResult(href, text)
                                    allResults.add(result)
                                    if (allResults.size == maxResults) {
                                        println("max results reached: ${allResults.size}")

                                        allResults.stream()
                                            .map { liResult ->
                                                BrowserPageUrl(
                                                    savedBrowserPage.id,
                                                    liResult.href,
                                                    liResult.text
                                                )
                                            }
                                            .forEach { bpu -> browserPageUrlRepository.save(bpu) }

                                        return allResults
                                    }
                                }
                            }
                    }
                    println("all results: ${allResults.size}")
                    Thread.sleep(scrollSleepInterval)
                    currentScrollHeight += scrollIncrement
                }
                println("Finished scrolling; looking for paging button for page ${nextPage}")

                // page navigation
                val buttonSelector = "button[aria-label='Page $nextPage']"
                val button = page.querySelector(buttonSelector)

                if (button != null) {
                    println("Found paging button for page $nextPage")
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

    fun cookie(): String {
        return """
            lms_ads=AQGc2EiQyv7RgAAAAZYmI14H8y55uPBMbLVRmLjf3O5J0YD-t3sx9ttzI_1iNj-y4apWQUKRpeMCpqzDcc__Qdz_ayMrBaAC;_guid=a7f7e7b4-25ba-4a5e-af9c-8c0c425997c2;bcookie="v=2&b02c8c08-170b-4ea0-831e-facdb3d737b9";li_ep_auth_context=AFlhcHA9YWNjb3VudENlbnRlckh1YixhaWQ9MjUwNTgyOTYyLGlpZD0yNzM5MTE5NjIscGlkPTI0NDIyODEyOCxleHA9MTc0NDQ5NDMxMjgyNixjdXI9dHJ1ZQHZm014yNUg1UxjmI83hRvHsA2GCw;lms_analytics=AQGc2EiQyv7RgAAAAZYmI14H8y55uPBMbLVRmLjf3O5J0YD-t3sx9ttzI_1iNj-y4apWQUKRpeMCpqzDcc__Qdz_ayMrBaAC;fptctx2=taBcrIH61PuCVH7eNCyH0J9Fjk1kZEyRnBbpUW3FKs8CHPOIZ3JaRzj0TmqOgi5YqpER%252fkNeuGfec9qPPIOTsZZ9QFCo4n7t5zymk60i7ir95%252fVy8EydGX3u%252fEbUWhagdpSQbXHkt7lU8FgBZZ7nZ2UyOB1Tb5zmjLtJhTdpqKCwqSkWPBt6o44%252bop69JDXNKKxxhCyo%252fEiWQkvurligmpl3wZSa0pQuaXcAcoketM%252f2334t3qbtLAukbcH0HVxH6iYblKzpFQdWqO6ECwOcZ2%252f7dZvDsu%252b00cpZ4U0NYI18r6gehmmNxEHcI5NiBy6CtF5Oo9BJq0W0CNdTBZTG0XMhvU4l2BvNSK4mrwrp07I%253d;li_at=AQEFAP4BAAAAABUsbjsAAAGVtD86SAAAAZYr9r1RVgAAoXVybjpsaTplbnRlcnByaXNlQXV0aFRva2VuOmVKeGpaQUFDdnZmVE40SHB2Z1VLSUZvZ2VNSXNJQ1VKQUU5cEJVbz1edXJuOmxpOmVudGVycHJpc2VQcm9maWxlOih1cm46bGk6ZW50ZXJwcmlzZUFjY291bnQ6MjUwNTgyOTYyLDI0NDIyODEyOCledXJuOmxpOm1lbWJlcjo3NDUwMjg0ZUM0BYKbrtcSK_DDv6rcN9o17X_Qb_OYeFS7dTzKaQC_YxnXbMzc1EJWuosGEL4ArMWq8mdtSah3_UBUXZP24HqRuRIRqxhd9_-avuIlLxKqOVaXwJ1YDIbX7xyZ8v5XsvPNefTXatTqpKDEr002jIpjWUabLoTLoXJgqdqxSMHOt7OsOSjpp8oy2o8kjkbjHYJRig;lang=v=2&lang=en-us;lidc="b=OB84:s=O:r=O:a=O:p=O:g=3653:u=1179:x=1:i=1744489919:t=1744523472:v=2:sig=AQGp78Zw_PzVPNZSfbn0Zh2lgqbVYSIB";AnalyticsSyncHistory=AQJOrmac3kVBdAAAAZYmI13jMU8GIT_C65ygv_H1XuCqockWWkn-Y9kvqiuxd5r6BoiCA0_1HRspfQEm7nO2gw;bscookie="v=1&202412050300057240bb5c-2ca0-4fce-8414-8baaed50d544AQFug22dAVCIwD0LyMmI3hbfhyAU4xJt";dfpfpt=1f65c80d88c04ea6831ccfd6ebefbccb;JSESSIONID="ajax:7108999861729582947";li_rm=AQHxwZ2uUTmpbgAAAZWlLWZ7c_Ic4JdOpwpwDld6Exb4wk-92g0VJZKyuI-WHd7Sy9MsUkgknGePLeMK9tHKfZVXAJRayzSXEBXQYWTHxglHwR_SPnW5-kaX;li_sugr=0f0ba9e2-93ce-42ab-a34c-873edbf80ec6;li_theme=light;li_theme_set=app;liap=true;timezone=America/Los_Angeles;UserMatchHistory=AQLffODoXeWZUgAAAZYrs7EHwdcQJGehpqnkgVAVGQEnpUDN8PFyXSn-D7LJWGfK3ZnHhSc7DhCpNMsVs6UN3oS1_2puBoiMchnruhtk9HyCTLadCalcCGjvWGlGhfRpwBev5fHHApLC2k6-RJ1mZ06p6O4cXRPD8FxE1ZdfD3JhAvtq52xqOOTcy4cZpuHF_ECYz6xVqUVnQe2EEGcAeKJL96lLZj8mUn8iDJmMX4vM3PmXHvGM0NIksuB9Ne_umMRcamIJufECbUqNIWOHrd82-ECo_oCSuQDyCJNiJfrV2vj-ygt3DzpYkvu2r_6YI3sqv-OVDi6YCMfUDCiTBGsHAVgax8CP9Kr63wg2jcFJdakGgQ;visit=v=1&M
        """.trimIndent()
    }
}