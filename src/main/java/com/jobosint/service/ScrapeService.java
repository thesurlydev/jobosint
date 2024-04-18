package com.jobosint.service;

import com.jobosint.config.ScrapeConfig;
import com.jobosint.model.FetchAttribute;
import com.jobosint.model.ScrapeRequest;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.model.SelectAttribute;
import com.jobosint.util.FileUtils;
import com.jobosint.utils.CookieUtilsKt;
import com.microsoft.playwright.*;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class ScrapeService {

    private final ScrapeConfig config;
    private final Browser browser;

    public ScrapeResponse scrapeHtml(String url) {
        return downloadAs(url, FetchAttribute.html);
    }

    public ScrapeResponse downloadAsPdf(String url) {
        return downloadAs(url, FetchAttribute.pdf);
    }

    public ScrapeResponse downloadAsHar(String url) {
        return downloadAs(url, FetchAttribute.har);
    }

    public ScrapeResponse downloadAsScreenshot(String url) {
        return downloadAs(url, FetchAttribute.screenshot);
    }

    private ScrapeResponse downloadAs(String url, FetchAttribute fetchAttribute) {
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        ScrapeRequest req = new ScrapeRequest(url, "html", SelectAttribute.html, null, Set.of(fetchAttribute));
        return scrape(req);
    }

    public ScrapeResponse scrape(ScrapeRequest req) throws PlaywrightException {
        var downloadPath = config.downloadPath();
        var namespace = config.namespace();
        var url = req.url();
        var slug = slugify(url);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (req.fetchHar()) {
            Path harPath = downloadPath.resolve(Path.of(namespace, slug, config.harFilename()));
            contextOptions.setRecordHarPath(harPath);
        }

        String host = URI.create(url).getHost();
        log.info("Getting cookies for host: {}", host);
        List<Cookie> cookiesForHost = CookieUtilsKt.getCookiesForHost(host);

        log.info("Found {} cookies for {}", cookiesForHost.size(), host);

        ScrapeResponse sr = null;
        EnumMap<FetchAttribute, String> downloadPaths = new EnumMap<>(FetchAttribute.class);
        try (BrowserContext context = browser.newContext(contextOptions)) {
            context.addCookies(cookiesForHost);
            Page page = context.newPage();
            page.onDOMContentLoaded(p -> {
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
                    downloadPaths.put(FetchAttribute.screenshot, ssPath.toFile().getAbsolutePath());
                }
                if (req.fetchHar()) {
                    Path harPath = downloadPath.resolve(Path.of(namespace, slug, config.harFilename()));
                    Page.RouteFromHAROptions routeFromHAROptions = new Page.RouteFromHAROptions();
                    routeFromHAROptions.setUpdate(true);
                    routeFromHAROptions.setUpdateContent(RouteFromHarUpdateContentPolicy.ATTACH);
                    p.routeFromHAR(harPath, routeFromHAROptions);
                    downloadPaths.put(FetchAttribute.har, harPath.toFile().getAbsolutePath());
                }
            });

            Response resp = page.navigate(url);
            if (!resp.ok()) {
                return new ScrapeResponse(req, Set.of(resp.statusText()), null, null, getBaseUrl(req.url()));
            }

            sr = scrape(req, page.content(), downloadPaths);
        } catch (Exception e) {
            log.error("Error during scraping: {}", req.toString(), e);
        }
        return sr;
    }

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

    public String getBaseUrl(String s) {
        String baseUrl = null;
        try {
            URI uri = new URI(s);
            URL url = uri.toURL();
            int port = url.getPort();
            if (port == -1) {
                baseUrl = url.getProtocol() + "://" + url.getHost();
            } else {
                baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + port;
            }
        } catch (URISyntaxException | MalformedURLException e) {
            log.warn("Unable to parse base url from url: {}", s);
        }
        return baseUrl;
    }

    private String slugify(String url) {
        if (url == null) {
            return "";
        }
        return url.replaceFirst("https://", "").replaceAll("[^a-zA-Z0-9]", "_");
    }
}
