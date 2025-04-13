package com.jobosint.service;

import com.jobosint.config.ScrapeConfig;
import com.jobosint.model.*;
import com.jobosint.parse.GenericHtmlParser;
import com.jobosint.parse.HtmlParser;
import com.jobosint.parse.ParseResult;
import com.jobosint.util.FileUtils;
import com.jobosint.util.LinkedInUtils;
import com.jobosint.util.UrlUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.RouteFromHarUpdateContentPolicy;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.jobosint.model.FetchAttribute.*;
import static com.jobosint.util.UrlUtils.getBaseUrl;

@SuppressWarnings("CssInvalidPseudoSelector")
@Service
@Log4j2
@RequiredArgsConstructor
public class ScrapeService {

    private final ScrapeConfig config;
    
    // Thread-local Playwright instances as per documentation
    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    
    // Track resources for cleanup
    private final Set<Long> initializedThreads = Collections.synchronizedSet(new HashSet<>());
    
    // Maximum number of retries for browser operations
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;

    @SuppressWarnings("unused")
    public ScrapeResponse scrapeHtml(String url) {
        return scrape(url, html);
    }

    public ScrapeResponse scrapeHtml(String url, List<Map<String, String>> cookies) {
        return scrape(url, cookies, html);
    }

    public ScrapeResponse scrapeText(String url, List<Map<String, String>> cookies) {
        return scrape(url, cookies, text);
    }

    @SuppressWarnings("unused")
    public ScrapeResponse pdf(String url) {
        return scrape(url, pdf);
    }

    @SuppressWarnings("unused")
    public ScrapeResponse har(String url) {
        return scrape(url, har);
    }

    @SuppressWarnings("unused")
    public ScrapeResponse screenshot(String url) {
        return scrape(url, screenshot);
    }

    @SuppressWarnings("unused")
    public ScrapeResponse scrape(String url, FetchAttribute... fetchAttributes) {
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        var attributes = Arrays.stream(fetchAttributes).collect(Collectors.toSet());
        ScrapeRequest req = new ScrapeRequest(url, "html", SelectAttribute.html, null, attributes);
        return scrape(req);
    }

    public ScrapeResponse scrape(String url, List<Map<String, String>> cookies, FetchAttribute... fetchAttributes) {
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        var attributes = Arrays.stream(fetchAttributes).collect(Collectors.toSet());
        ScrapeRequest req = new ScrapeRequest(url, "html", SelectAttribute.html, null, attributes, cookies);
        return scrape(req);
    }

    /**
     * Get the Playwright instance for the current thread.
     * Each thread gets its own Playwright instance as per the documentation.
     */
    private Playwright getPlaywright() {
        Playwright playwright = PLAYWRIGHT_THREAD_LOCAL.get();
        if (playwright == null) {
            playwright = Playwright.create();
            PLAYWRIGHT_THREAD_LOCAL.set(playwright);
            initializedThreads.add(Thread.currentThread().getId());
            log.info("Created new Playwright instance for thread {}", Thread.currentThread().getId());
        }
        return playwright;
    }

    /**
     * Get the Browser instance for the current thread
     */
    private Browser getBrowser() {
        Browser browser = BROWSER_THREAD_LOCAL.get();
        if (browser == null) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(true);
            browser = getPlaywright().chromium().launch(launchOptions);
            BROWSER_THREAD_LOCAL.set(browser);
            log.info("Created new browser instance for thread {}", Thread.currentThread().getId());
        }
        return browser;
    }

    /**
     * Create a new browser context for the current thread
     */
    private BrowserContext createContext(Browser.NewContextOptions contextOptions) {
        // Close any existing context for this thread
        closeContext();
        
        // Create a new context with retry mechanism
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                BrowserContext context = getBrowser().newContext(contextOptions);
                CONTEXT_THREAD_LOCAL.set(context);
                log.debug("Created new browser context for thread {} (attempt {})", 
                        Thread.currentThread().getId(), attempt + 1);
                return context;
            } catch (PlaywrightException e) {
                if (attempt == MAX_RETRIES - 1) {
                    // Last attempt failed, rethrow
                    throw e;
                }
                
                log.warn("Error creating browser context for thread {} (attempt {}): {}", 
                        Thread.currentThread().getId(), attempt + 1, e.getMessage());
                
                // If browser seems to be in a bad state, recreate it
                if (e.getMessage().contains("Cannot find object") || 
                    e.getMessage().contains("Object doesn't exist")) {
                    
                    closeBrowser();
                    getBrowser(); // Create a new browser
                }
                
                // Wait before retrying
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry context creation", ie);
                }
            }
        }
        
        // Should never reach here due to exception in last retry
        throw new RuntimeException("Failed to create browser context after " + MAX_RETRIES + " attempts");
    }
    
    /**
     * Close the browser context for the current thread
     */
    private void closeContext() {
        BrowserContext existingContext = CONTEXT_THREAD_LOCAL.get();
        if (existingContext != null) {
            try {
                existingContext.close();
                CONTEXT_THREAD_LOCAL.remove();
                log.debug("Closed browser context for thread {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.warn("Error closing browser context for thread {}: {}", 
                        Thread.currentThread().getId(), e.getMessage());
            }
        }
    }
    
    /**
     * Close the browser for the current thread
     */
    private void closeBrowser() {
        // First close any existing context
        closeContext();
        
        // Then close the browser
        Browser browser = BROWSER_THREAD_LOCAL.get();
        if (browser != null) {
            try {
                browser.close();
                BROWSER_THREAD_LOCAL.remove();
                log.info("Closed browser for thread {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.warn("Error closing browser for thread {}: {}", 
                        Thread.currentThread().getId(), e.getMessage());
            }
        }
    }
    
    /**
     * Close the Playwright instance for the current thread
     */
    private void closePlaywright() {
        // First close browser and context
        closeBrowser();
        
        // Then close Playwright
        Playwright playwright = PLAYWRIGHT_THREAD_LOCAL.get();
        if (playwright != null) {
            try {
                playwright.close();
                PLAYWRIGHT_THREAD_LOCAL.remove();
                log.info("Closed Playwright instance for thread {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.warn("Error closing Playwright instance for thread {}: {}", 
                        Thread.currentThread().getId(), e.getMessage());
            }
        }
    }

    @SuppressWarnings("unused")
    public ScrapeResponse scrape(ScrapeRequest req) throws PlaywrightException {
        var downloadPath = config.downloadPath();
        var namespace = config.namespace();
        var url = req.url();
        String slug;
        if (url.startsWith("https://www.linkedin.com/jobs/view/")) {
            var jobId = LinkedInUtils.INSTANCE.getJobBoardIdFromUrl(url);
            slug = "linkedin-job-" + jobId;
        } else {
            slug = UUID.randomUUID().toString();
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (req.fetchHar()) {
            Path harPath = downloadPath.resolve(Path.of(namespace, slug, config.harFilename()));
            contextOptions.setRecordHarPath(harPath);
        }
        if (config.viewportWidth() != null && config.viewportHeight() != null) {
            contextOptions.setViewportSize(config.viewportWidth(), config.viewportHeight());
        }

        List<Cookie> cookiesForHost = null;

        if (config.cookiesEnabled() != null && config.cookiesEnabled() && req.cookies() != null) {
            cookiesForHost = new ArrayList<>();

            for (Map<String, String> cookieMap : req.cookies()) {
                if (cookieMap.containsKey("name") && cookieMap.containsKey("value")) {
                    Cookie cookie = new Cookie(cookieMap.get("name"), cookieMap.get("value"));

                    // Set required domain and path properties
                    if (cookieMap.containsKey("domain")) {
                        cookie.domain = cookieMap.get("domain");
                    }

                    if (cookieMap.containsKey("path")) {
                        cookie.path = cookieMap.get("path");
                    }

                    // Set optional properties
                    if (cookieMap.containsKey("expirationDate")) {
                        try {
                            cookie.expires = Double.parseDouble(cookieMap.get("expirationDate"));
                        } catch (NumberFormatException e) {
                            log.warn("Invalid expirationDate format for cookie: {}", cookieMap.get("name"));
                        }
                    }

                    if (cookieMap.containsKey("httpOnly")) {
                        cookie.httpOnly = Boolean.parseBoolean(cookieMap.get("httpOnly"));
                    }

                    if (cookieMap.containsKey("secure")) {
                        cookie.secure = Boolean.parseBoolean(cookieMap.get("secure"));
                    }

                    if (cookieMap.containsKey("sameSite")) {
                        String sameSite = cookieMap.get("sameSite");
                        if ("lax".equalsIgnoreCase(sameSite)) {
                            cookie.sameSite = com.microsoft.playwright.options.SameSiteAttribute.LAX;
                        } else if ("strict".equalsIgnoreCase(sameSite)) {
                            cookie.sameSite = com.microsoft.playwright.options.SameSiteAttribute.STRICT;
                        } else if ("none".equalsIgnoreCase(sameSite) || "no_restriction".equalsIgnoreCase(sameSite)) {
                            cookie.sameSite = com.microsoft.playwright.options.SameSiteAttribute.NONE;
                        }
                    }

                    cookiesForHost.add(cookie);
                }
            }

            log.info("Found {} cookies", cookiesForHost.size());
        }

        ScrapeResponse sr;
        Set<String> errors = new HashSet<>();
        EnumMap<FetchAttribute, String> downloadPaths = new EnumMap<>(FetchAttribute.class);
        
        // Implement retry mechanism for the entire scrape operation
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                // Create a new context for this request
                BrowserContext context = createContext(contextOptions);
                
                if (config.cookiesEnabled() != null && config.cookiesEnabled() && cookiesForHost != null) {
                    context.addCookies(cookiesForHost);
                }
                
                Page page = context.newPage();

                if (config.defaultTimeoutMillis() != null) {
                    page.setDefaultTimeout(config.defaultTimeoutMillis());
                }

                page.onDOMContentLoaded(p -> handleContent(req, p, downloadPath, namespace, slug, downloadPaths));

                Response resp = page.navigate(url);
                if (!resp.ok()) {
                    closeContext();
                    return new ScrapeResponse(req, Set.of(resp.statusText()), null, null, getBaseUrl(req.url()));
                }

                String content = page.content();
                sr = scrape(req, content, downloadPaths);
                
                // Always close the context when done
                closeContext();
                
                // Success, return the result
                return sr;
                
            } catch (Exception e) {
                // Make sure to close the context even if there's an error
                closeContext();
                
                if (attempt == MAX_RETRIES - 1) {
                    // Last attempt, add error and return error response
                    errors.add(String.format("Error scraping: error=%s, url=%s", e.getMessage(), req));
                    return new ScrapeResponse(req, errors, null, downloadPaths, getBaseUrl(req.url()));
                }
                
                log.warn("Error during scrape attempt {} for {}: {}", attempt + 1, url, e.getMessage());
                
                // If browser seems to be in a bad state, recreate it
                if (e.getMessage() != null && (
                    e.getMessage().contains("Cannot find object") || 
                    e.getMessage().contains("Object doesn't exist"))) {
                    
                    closeBrowser();
                }
                
                // Wait before retrying
                try {
                    Thread.sleep(RETRY_DELAY_MS * (attempt + 1)); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    errors.add("Interrupted while waiting to retry scrape: " + ie.getMessage());
                    return new ScrapeResponse(req, errors, null, downloadPaths, getBaseUrl(req.url()));
                }
            }
        }
        
        // Should never reach here due to return in last retry attempt
        errors.add("Failed to scrape after " + MAX_RETRIES + " attempts");
        return new ScrapeResponse(req, errors, null, downloadPaths, getBaseUrl(req.url()));
    }

    private void handleContent(ScrapeRequest req,
                               Page p,
                               Path downloadPath,
                               String namespace,
                               String slug,
                               EnumMap<FetchAttribute, String> downloadPaths) {
        String html = p.content();
        if (req.fetchHtml()) {
            Path htmlPath = downloadPath.resolve(Path.of(namespace, slug, config.htmlFilename()));
            FileUtils.writeToFile(htmlPath.toString(), html);
            downloadPaths.put(FetchAttribute.html, htmlPath.toString());
        }

        if (req.fetchText()) {
            Path textPath = downloadPath.resolve(Path.of(namespace, slug, config.textFilename()));
            String text = p.evaluate("document.body.innerText").toString();
            FileUtils.writeToFile(textPath.toString(), text);
            downloadPaths.put(FetchAttribute.text, textPath.toString());
        }

        if (req.fetchScreenshot()) {
            Path screenshotPath = downloadPath.resolve(Path.of(namespace, slug, config.screenshotFilename()));
            byte[] screenshot = p.screenshot();
            // Convert screenshot bytes to Base64 string for storage
            String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);
            FileUtils.writeToFile(screenshotPath.toString(), base64Screenshot);
            downloadPaths.put(FetchAttribute.screenshot, screenshotPath.toString());
        }

        if (req.fetchPdf()) {
            Path pdfPath = downloadPath.resolve(Path.of(namespace, slug, config.pdfFilename()));
            byte[] pdf = p.pdf();
            // Convert PDF bytes to Base64 string for storage
            String base64Pdf = Base64.getEncoder().encodeToString(pdf);
            FileUtils.writeToFile(pdfPath.toString(), base64Pdf);
            downloadPaths.put(FetchAttribute.pdf, pdfPath.toString());
        }

        if (req.fetchHar()) {
            Path harPath = downloadPath.resolve(Path.of(namespace, slug, config.harFilename()));
            downloadPaths.put(FetchAttribute.har, harPath.toString());
        }
    }

    private ScrapeResponse scrape(ScrapeRequest req, String html, EnumMap<FetchAttribute, String> downloadPaths) {
        List<String> dataList = new ArrayList<>();
        Set<String> errors = new HashSet<>();

        try {
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(req.selector());
            if (elements.isEmpty()) {
                errors.add("No elements found matching selector: " + req.selector());
            } else {
                for (Element element : elements) {
                    if (req.attribute() == null || req.attribute() == SelectAttribute.html) {
                        dataList.add(element.html());
                    } else if (req.attribute() == SelectAttribute.text) {
                        dataList.add(element.text());
                    } else if (req.attribute() == SelectAttribute.attr && req.attributeValue() != null) {
                        dataList.add(element.attr(req.attributeValue()));
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Error parsing HTML: " + e.getMessage());
        }

        // Convert List<String> to Set<String> for the ScrapeResponse
        Set<String> dataSet = new HashSet<>(dataList);
        return new ScrapeResponse(req, errors.isEmpty() ? null : errors, dataSet, downloadPaths, getBaseUrl(req.url()));
    }

    @SuppressWarnings("unused")
    public ParseResult parse(String url, String html) {
        HtmlParser parser = new GenericHtmlParser();
        return parser.parse(url, html);
    }
    
    /**
     * Cleanup all browser resources when the application shuts down
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up browser resources...");
        
        // Clean up resources for all threads that we've initialized
        for (Long threadId : initializedThreads) {
            log.info("Cleaning up resources for thread {}", threadId);
            
            // We can't access ThreadLocal variables from other threads,
            // so we'll just log that cleanup is needed
            log.info("Resources for thread {} will be garbage collected", threadId);
        }
        
        // Clean up for the current thread
        closePlaywright();
        
        log.info("Browser cleanup complete");
    }
}
