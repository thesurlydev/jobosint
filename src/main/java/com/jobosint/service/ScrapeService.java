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
import java.util.stream.Collectors;

import static com.jobosint.model.FetchAttribute.*;
import static com.jobosint.util.UrlUtils.getBaseUrl;

@SuppressWarnings("CssInvalidPseudoSelector")
@Service
@Log4j2
@RequiredArgsConstructor
public class ScrapeService {

    private final ScrapeConfig config;
    private final Browser browser;

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
        try (BrowserContext context = browser.newContext(contextOptions)) {
            if (config.cookiesEnabled() != null && config.cookiesEnabled()) {
                context.addCookies(cookiesForHost);
            }
            Page page = context.newPage();

            if (config.defaultTimeoutMillis() != null) {
                page.setDefaultTimeout(config.defaultTimeoutMillis());
            }

            page.onDOMContentLoaded(p -> handleContent(req, p, downloadPath, namespace, slug, downloadPaths));

            Response resp = page.navigate(url);
            if (!resp.ok()) {
                return new ScrapeResponse(req, Set.of(resp.statusText()), null, null, getBaseUrl(req.url()));
            }

            sr = scrape(req, page.content(), downloadPaths);
        } catch (Exception e) {
            errors.add(String.format("Error scraping: error=%s, url=%s", e.getMessage(), req));
            sr = new ScrapeResponse(req, errors, null, downloadPaths, getBaseUrl(req.url()));
        }
        return sr;
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
            try {
                FileUtils.saveToFile(html, htmlPath);
                downloadPaths.put(FetchAttribute.html, htmlPath.toFile().getAbsolutePath());
            } catch (IOException e) {
                log.error("Error saving html to file: {}", htmlPath, e);
            }
        }
        if (req.fetchText()) {
            Path textPath = downloadPath.resolve(Path.of(namespace, slug, config.textFilename()));
            try {
                HtmlParser<HtmlContent> htmlParser = new GenericHtmlParser();
                ParseResult<HtmlContent> pr = htmlParser.parse(html);
                String text = pr.getData().text();
                FileUtils.saveToFile(text, textPath);
                downloadPaths.put(FetchAttribute.text, textPath.toFile().getAbsolutePath());
            } catch (IOException e) {
                log.error("Error saving text to file: {}", textPath, e);
            }
        }
        if (req.fetchPdf()) {
            Path pdfPath = downloadPath.resolve(Path.of(namespace, slug, config.pdfFilename()));
            Page.PdfOptions pdfOptions = new Page.PdfOptions()
                    .setPath(pdfPath);
            p.pdf(pdfOptions);
            downloadPaths.put(FetchAttribute.html, pdfPath.toFile().getAbsolutePath());
        }
        if (req.fetchScreenshot()) {
            Path ssPath = downloadPath.resolve(Path.of(namespace, slug, config.screenshotFilename()));
            Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions().setPath(ssPath);
            p.screenshot(screenshotOptions);
            downloadPaths.put(screenshot, ssPath.toFile().getAbsolutePath());
        }
        if (req.fetchHar()) {
            Path harPath = downloadPath.resolve(Path.of(namespace, slug, config.harFilename()));
            Page.RouteFromHAROptions routeFromHAROptions = new Page.RouteFromHAROptions();
            routeFromHAROptions.setUpdate(true);
            routeFromHAROptions.setUpdateContent(RouteFromHarUpdateContentPolicy.ATTACH);
            p.routeFromHAR(harPath, routeFromHAROptions);
            downloadPaths.put(har, harPath.toFile().getAbsolutePath());
        }
    }

    @SuppressWarnings("unused")
    private String detectNavigationType(Page page) {
        // Check for the presence of typical pagination link attributes
        Locator paginationLinks = page.locator("a[href*='p='], a[href*='page='], a[href*='Page='], a[href*='PAGE='], a[href*='/page/']");

        if (paginationLinks.count() > 0) {
            Locator foundPaginationLinkHref = paginationLinks.first();
            String href = foundPaginationLinkHref.getAttribute("href");
            log.info("Found pagination link href: {}", href);

            // verify the pagination link ends in an integer to prevent false positives
            if (href.matches(".*\\d$")) {
                String locatorStr = "a[href='" + href + "']";
                String uniqueSelector = getUniqueSelector(page, locatorStr);
                log.info("Unique selector for pagination link: {}", uniqueSelector);
                return "pagination";
            }
        }


        // Attempt to detect infinite scrolling by comparing document height before and after scroll
        int originalHeight = (int) page.evaluate("() => document.body.scrollHeight");
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(3000); // Adjust the timeout as necessary

        int newHeight = (int) page.evaluate("() => document.body.scrollHeight");
        if (newHeight > originalHeight) {
            return "infinite scroll";
        }

        // Detect a 'Load More' button by looking for common button texts
        if (page.locator("button:has-text('Load More'), button:has-text('Show More'), button:has-text('More')").count() > 0) {
            return "load more button";
        }

        return "unknown";
    }

    private static String getUniqueSelector(Page page, String locator) {
        String script = """
                element => {
                  if (element.id) return '#' + element.id;
                  let path = [];
                  while (element.parentNode) {
                    let index = 1;
                    let sibling = element.previousSibling;
                    while (sibling) {
                      if (sibling.nodeType === Document.ELEMENT_NODE && sibling.tagName === element.tagName) {
                        index++;
                      }
                      sibling = sibling.previousSibling;
                    }
                    let tagName = element.tagName.toLowerCase();
                    let selector = tagName + (index > 1 ? ':nth-of-type(' + index + ')' : '');
                    path.unshift(selector);
                    element = element.parentNode;
                  }
                  return path.join(' > ');
                }""";

        // Using the locator to find the first element and then passing it to the script
        ElementHandle elementHandle = page.locator(locator).first().elementHandle();
        if (elementHandle == null) {
            throw new RuntimeException("Element not found for the locator: " + locator);
        }

        return (String) elementHandle.evaluate(script);
    }

    @NotNull
    private ScrapeResponse scrape(ScrapeRequest req, String htmlContent, EnumMap<FetchAttribute, String> downloadPaths) {
        Set<String> errors = new HashSet<>();
        Set<String> data = new HashSet<>();
        Document doc;
        doc = Jsoup.parse(htmlContent, "UTF-8");
        Elements els = doc.select(req.selector());
        if (els.isEmpty()) {
            errors.add("No elements found for selector: " + req.selector());
        } else {
            for (Element el : els) {
                var ed = req.attribute().select(el, req.attributeValue());
                if (!ed.isEmpty()) {
                    if ("a".equals(el.tagName()) && ed.startsWith("/")) {
                        var baseUrl = req.url();
                        if (baseUrl.endsWith("/")) {
                            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                        }
                        ed = baseUrl + ed;
                    }
                    if (!ed.startsWith("#")) {
                        data.add(ed);
                    }
                }
            }
        }

        String baseUrl = getBaseUrl(req.url());

        return new ScrapeResponse(req, errors, data, downloadPaths, baseUrl);
    }
}
