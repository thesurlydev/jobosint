package com.jobosint.listener;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.event.BrowserPageUrlCreatedEvent;
import com.jobosint.model.Job;
import com.jobosint.model.Page;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.playwright.CookieService;
import com.jobosint.service.JobService;
import com.jobosint.service.LinkedInService;
import com.jobosint.service.PageService;
import com.jobosint.service.ScrapeService;
import com.jobosint.util.LinkedInUtils;
import com.jobosint.util.UrlUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@Component
@RequiredArgsConstructor
@Slf4j
public class BrowserPageUrlCreatedEventListener implements ApplicationListener<BrowserPageUrlCreatedEvent> {

    private final HttpClientFactory httpClientFactory;
    private final CookieService cookieService;
    private final PageService pageService;
    private final ScrapeService scrapeService;
    private final LinkedInService linkedInService;
    private final JobService jobService;

    @Override
    public void onApplicationEvent(@NonNull BrowserPageUrlCreatedEvent event) {
        log.info("Received: {}", event);

        var url = event.getBrowserPageUrl().url();

        var jobId = LinkedInUtils.INSTANCE.getJobBoardIdFromUrl(url);
        Optional<Job> existingJob = jobService.getJobBySourceJobId("LinkedIn", jobId);
        if (existingJob.isPresent()) {
            log.info("Job already exists: {}", existingJob.get());
            return;
        }

        log.info("Scraping URL: {}", url);
        Path savedPath;
        try {

            ScrapeResponse scrapeResponse = scrapeService.scrapeHtml(url, cookieService.loadLinkedInCookies());

            if (scrapeResponse.errors() == null || !scrapeResponse.errors().isEmpty()) {
                log.error("Errors: {}", scrapeResponse.errors());
                throw new RuntimeException("Failed to get job detail: " + scrapeResponse.errors());
            }

            var content = java.lang.String.join(System.lineSeparator(), scrapeResponse.data());

            String decodedContent = UrlUtils.decodeContent(content);

            Document doc = Jsoup.parse(decodedContent);
            if (doc.title().equals("LinkedIn")) {
                throw new RuntimeException("LinkedIn blocked the request");
            }

            savedPath = pageService.saveContent(decodedContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Saved content to {}", savedPath);

        Page page = new Page(null, url, savedPath.toString(), "sync");
        Page savedPage = pageService.savePage(page, null);

        log.info("Saved page: {}", savedPage);
    }

    /**
     * Returns the standard headers needed for LinkedIn requests to mimic a browser.
     * These should be applied to each request made with the LinkedIn client.
     *
     * @return Map of header names to values
     */
    public Map<String, String> getLinkedInHeaders() {
        return Map.ofEntries(
                entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
                entry("Accept-Language", "en-US,en;q=0.9"),
//                entry("Cache-Control", "no-cache"),
//                entry("Pragma", "no-cache"),
//                entry("priority", "u=0, i"),
//                entry("sec-ch-prefers-color-scheme", "light"),
//                entry("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\""),
//                entry("sec-ch-ua-mobile", "?0"),
//                entry("sec-ch-ua-platform", "\"macOS\""),
//                entry("sec-fetch-dest", "document"),
//                entry("sec-fetch-mode", "navigate"),
//                entry("sec-fetch-site", "same-origin"),
//                entry("sec-fetch-user", "?1"),
//                entry("upgrade-insecure-requests", "1"),
                entry("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
        );
    }
}
