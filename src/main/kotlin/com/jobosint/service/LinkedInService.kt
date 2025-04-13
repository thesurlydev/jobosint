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
import com.jobosint.util.LinkedInUtils.getCompanyTokenFromUrl
import com.jobosint.util.LinkedInUtils.getJobBoardIdFromPath
import com.jobosint.util.LinkedInUtils.getJobBoardIdFromUrl
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
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
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import jakarta.annotation.PreDestroy

@Service
class LinkedInService(
    val playwright: Playwright,
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
    
    // Thread-local browser instances
    private val threadBrowsers = ConcurrentHashMap<Long, Browser>()
    private val threadContexts = ConcurrentHashMap<Long, BrowserContext>()

    /**
     * Get or create a browser instance for the current thread
     */
    private fun getBrowser(): Browser {
        val threadId = Thread.currentThread().id
        return threadBrowsers.computeIfAbsent(threadId) {
            log.info("Creating new browser instance for thread {}", threadId)
            val launchOptions = LaunchOptions()
            launchOptions.setHeadless(true)
            playwright.chromium().launch(launchOptions)
        }
    }

    /**
     * Create a new browser context for the current thread
     */
    private fun createContext(): BrowserContext {
        val threadId = Thread.currentThread().id
        // Close any existing context for this thread
        closeContext()
        
        // Create a new context
        val context = getBrowser().newContext()
        threadContexts[threadId] = context
        log.debug("Created new browser context for thread {}", threadId)
        return context
    }
    
    /**
     * Close the browser context for the current thread
     */
    private fun closeContext() {
        val threadId = Thread.currentThread().id
        val existingContext = threadContexts.remove(threadId)
        existingContext?.let {
            try {
                it.close()
                log.debug("Closed browser context for thread {}", threadId)
            } catch (e: Exception) {
                log.warn("Error closing browser context for thread {}: {}", threadId, e.message)
            }
        }
    }
    
    /**
     * Clean up resources when the application is shutting down
     */
    @PreDestroy
    fun cleanup() {
        log.info("Cleaning up browser resources")
        threadContexts.forEach { (threadId, context) ->
            try {
                context.close()
                log.debug("Closed browser context for thread {}", threadId)
            } catch (e: Exception) {
                log.warn("Error closing browser context for thread {}: {}", threadId, e.message)
            }
        }
        threadContexts.clear()
        
        threadBrowsers.forEach { (threadId, browser) ->
            try {
                browser.close()
                log.debug("Closed browser for thread {}", threadId)
            } catch (e: Exception) {
                log.warn("Error closing browser for thread {}: {}", threadId, e.message)
            }
        }
        threadBrowsers.clear()
    }

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
            val pageUrl = jobPageDetail.pageUrl
            try {
                val jobBoardId = getJobBoardIdFromUrl(pageUrl)
                if (jobBoardId != null) {
                    jobService.updateJobBoardId(jobPageDetail.id, jobBoardId)
                }
            } catch (e: Exception) {
                log.error("Error updating job board id for {}", pageUrl, e)
            }
        })
    }

    fun getJobDetail(jobId: String): Job? {
        val existingJob = jobService.getJobBySourceJobId("LinkedIn", jobId)
        if (existingJob.isPresent) {
            return existingJob.get()
        }

        val url = "https://www.linkedin.com/jobs/view/$jobId"
        val scrapeResponse = scrapeService.scrapeHtml(url, cookieService.loadLinkedInCookies())
        if (scrapeResponse.errors() != null) {
            log.error("Error scraping job detail: {}", scrapeResponse.errors())
            return null
        }

        val html = scrapeResponse.data().joinToString("\n")
        try {
            val parserResult = linkedInParser.parseJobDescriptionFromContent(html)
            // Create a new Job with the parsed data
            return Job(
                null,                      // id
                null,                      // companyId
                jobId,                     // jobBoardId
                parserResult.title,        // title
                url,                       // url
                null,                      // salaryMin
                null,                      // salaryMax
                "LinkedIn",                // source
                null,                      // notes
                parserResult.description,  // content
                "new",                     // status
                null,                      // pageId
                LocalDateTime.now()        // createdAt
            ).let { jobService.saveJob(it) }
        } catch (e: Exception) {
            log.error("Error parsing job detail: {}", e.message)
            return null
        }
    }

    fun getCompanyDetail(companyToken: String): Company? {
        val url = "https://www.linkedin.com/company/$companyToken"
        val scrapeResponse = scrapeService.scrapeHtml(url, cookieService.loadLinkedInCookies())
        if (scrapeResponse.errors() != null) {
            log.error("Error scraping company detail: {}", scrapeResponse.errors())
            return null
        }

        val html = scrapeResponse.data().joinToString("\n")
        try {
            val result = linkedInParser.parseCompanyDescriptionFromString(html)
            return Company(
                null,
                result.name,
                result.websiteUrl,
                null,
                result.employeeCount,
                result.summary,
                result.location,
                companyToken,
                null
            )
        } catch (e: Exception) {
            log.error("Error parsing company detail: {}", e.message)
            return null
        }
    }

    fun getCompanyJobs(companyToken: String): List<Job> {
        val url = "https://www.linkedin.com/company/$companyToken/jobs"
        val scrapeResponse = scrapeService.scrapeHtml(url, cookieService.loadLinkedInCookies())
        if (scrapeResponse.errors() != null) {
            log.error("Error scraping company jobs: {}", scrapeResponse.errors())
            return emptyList()
        }

        // This would require implementing a method to parse job listings from a company page
        // For now, we'll return an empty list
        return emptyList()
    }
    
    /**
     * Scrape a company profile from LinkedIn
     * 
     * @param companyTag The company tag/slug from the LinkedIn URL
     * @param cookies Optional cookies to use for the request
     * @return The company information or null if not found
     */
    fun scrapeCompany(companyTag: String, cookies: List<Map<String, String>>?): Company? {
        val url = "https://www.linkedin.com/company/$companyTag/about/"
        log.info("Scraping company: $url")
        
        val scrapeResponse = scrapeService.scrapeHtml(url, cookies)
        if (scrapeResponse.errors() != null) {
            log.error("Error scraping company: {}", scrapeResponse.errors())
            return null
        }
        
        if (scrapeResponse.data().isEmpty()) {
            return null
        }
        
        val content = scrapeResponse.data().joinToString("\n")
        try {
            val result = linkedInParser.parseCompanyDescriptionFromString(content)
            return Company(
                null,
                result.name,
                result.websiteUrl,
                null,
                result.employeeCount,
                result.summary,
                result.location,
                companyTag,
                null
            )
        } catch (e: Exception) {
            log.error("Error parsing company: {}", e.message)
            return null
        }
    }

    fun browse(url: String): BrowserSession {
        log.info("Browsing {}", url)
        val session = BrowserSession("LinkedInBrowse", url)
        val savedSession = browserSessionRepository.save(session)

        try {
            val context = createContext()
            val page = context.newPage()
            
            // Add cookies
            val cookiesList = cookieService.loadLinkedInCookies()
            if (cookiesList.isNotEmpty()) {
                val cookies = cookieService.mapCookiesToPlaywright(cookiesList)
                context.addCookies(cookies)
            }

            // Navigate to the URL
            page.navigate(url)
            page.waitForLoadState(LoadState.DOMCONTENTLOADED)

            // Create a browser page record
            val browserPage = BrowserPage(null, savedSession.id(), page.title(), "active")
            val savedPage = browserPageRepository.save(browserPage)

            // Extract and save URLs
            val links = page.querySelectorAll("a[href*='/jobs/view/']")
            for (link in links) {
                val href = link.getAttribute("href")
                if (href != null && href.contains("/jobs/view/")) {
                    val jobId = getJobBoardIdFromPath(href)
                    if (jobId != null) {
                        val fullUrl = if (href.startsWith("http")) href else "https://www.linkedin.com$href"
                        val text = link.textContent()
                        
                        val browserPageUrl = BrowserPageUrl(null, savedPage.id(), fullUrl, text)
                        val savedBrowserPageUrl = browserPageUrlRepository.save(browserPageUrl)
                        
                        // Publish event for further processing
                        applicationEventPublisher.publishEvent(
                            BrowserPageUrlCreatedEvent(this, savedBrowserPageUrl)
                        )
                    }
                }
            }

            closeContext()
            
            return savedSession
        } catch (e: Exception) {
            log.error("Error browsing {}", url, e)
            closeContext()
            throw RuntimeException("Error browsing $url", e)
        }
    }

    fun jobStillAcceptingApplications(jobId: String): Boolean {
        val url = String.format("https://www.linkedin.com/jobs/view/%s", jobId)
        val scrapeResponse: ScrapeResponse = scrapeService.scrapeHtml(url)
        val figure = scrapeResponse.data().filter { it.contains("No longer accepting applications") }
        return figure.isEmpty()
    }

    fun searchJobs(jobSearchRequest: LinkedInJobSearchRequest): Set<LinkedInResult> {
        val allResults = mutableSetOf<LinkedInResult>()

        val scrollIncrement = 300
        val scrollSleepInterval = 500L
        val term = jobSearchRequest.term
        val maxResults = jobSearchRequest.maxResults

        val context = createContext()
        val page = context.newPage()

        // Add cookies
        val cookiesList = cookieService.loadLinkedInCookies()
        if (cookiesList.isNotEmpty()) {
            val cookies = cookieService.mapCookiesToPlaywright(cookiesList)
            context.addCookies(cookies)
        }

        val startPageUrl = "https://www.linkedin.com/jobs/search/?f_TPR=r86400&f_WT=2&keywords=${term}"

        val browserSession = BrowserSession("LinkedInServiceTest", startPageUrl)
        val savedBrowserSession = browserSessionRepository.save(browserSession)
        log.info("Saved browser session: ${savedBrowserSession.id()}")

        page.navigate(startPageUrl)

        var nextPage = 2

        try {
            while (true) {
                log.info("Waiting for page ${nextPage - 1} to load")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)

                // TODO save page to disk and get path
                val browserPage = BrowserPage(savedBrowserSession.id(), page.url())
                val savedBrowserPage = browserPageRepository.save(browserPage)
                log.info("Saved browser page: ${savedBrowserPage.id()}")

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
                                    val text =
                                        link.getAttribute("aria-label").replace("with verification", "").trim()
                                    val result = LinkedInResult(href, text)
                                    allResults.add(result)
                                    if (allResults.size == maxResults) {
                                        log.info("max results reached: ${allResults.size}")

                                        allResults.stream()
                                            .map { liResult ->
                                                val jobId = getJobBoardIdFromPath(liResult.href)
                                                val url = "https://www.linkedin.com/jobs/view/$jobId"
                                                BrowserPageUrl(
                                                    savedBrowserPage.id(),
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

                                        closeContext()
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
        } finally {
            closeContext()
        }
        
        return allResults
    }
}