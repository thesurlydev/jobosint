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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    // Semaphore to limit concurrent scraping operations
    private final Semaphore scrapeSemaphore = new Semaphore(2); // Reduced to 2 concurrent scrapes
    
    // Track scraping statistics
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    @Override
    @Async
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
        Path savedPath = null;
        
        // Acquire a permit from the semaphore before scraping
        try {
            log.debug("Waiting for scrape permit for URL: {}", url);
            scrapeSemaphore.acquire();
            log.debug("Acquired scrape permit for URL: {}", url);
            
            try {
                // Attempt to scrape with retry logic
                ScrapeResponse scrapeResponse = null;
                Exception lastException = null;
                
                // Try up to 3 times
                for (int attempt = 0; attempt < 3; attempt++) {
                    try {
                        scrapeResponse = scrapeService.scrapeHtml(url, cookieService.loadLinkedInCookies());
                        break; // Success, exit the loop
                    } catch (Exception e) {
                        lastException = e;
                        log.warn("Scrape attempt {} failed for URL {}: {}", attempt + 1, url, e.getMessage());
                        
                        if (attempt < 2) {
                            // Wait before retrying
                            try {
                                Thread.sleep(1000 * (attempt + 1)); // Exponential backoff
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Interrupted while waiting to retry scrape", ie);
                            }
                        }
                    }
                }
                
                // If all attempts failed
                if (scrapeResponse == null) {
                    if (lastException != null) {
                        throw lastException;
                    } else {
                        throw new RuntimeException("Failed to scrape after multiple attempts");
                    }
                }

                if (scrapeResponse.errors() != null && !scrapeResponse.errors().isEmpty()) {
                    log.error("Errors: {}", scrapeResponse.errors());
                    failureCount.incrementAndGet();
                    throw new RuntimeException("Failed to get job detail: " + scrapeResponse.errors());
                }

                if (scrapeResponse.data() == null || scrapeResponse.data().isEmpty()) {
                    log.error("No data returned from scrape");
                    failureCount.incrementAndGet();
                    throw new RuntimeException("Failed to get job detail: No data returned");
                }

                var content = java.lang.String.join(System.lineSeparator(), scrapeResponse.data());

                String decodedContent = UrlUtils.decodeContent(content);

                Document doc = Jsoup.parse(decodedContent);
                if (doc.title().equals("LinkedIn")) {
                    failureCount.incrementAndGet();
                    throw new RuntimeException("LinkedIn blocked the request");
                }

                savedPath = pageService.saveContent(decodedContent);
                successCount.incrementAndGet();
                log.info("Scrape successful for URL: {} (Success: {}, Failures: {})", 
                        url, successCount.get(), failureCount.get());
                
            } catch (IOException e) {
                failureCount.incrementAndGet();
                log.error("IO error while scraping URL {}: {}", url, e.getMessage());
                throw new RuntimeException(e);
            } catch (Exception e) {
                failureCount.incrementAndGet();
                log.error("Error while scraping URL {}: {}", url, e.getMessage());
                throw new RuntimeException(e);
            } finally {
                // Always release the permit
                scrapeSemaphore.release();
                log.debug("Released scrape permit for URL: {}", url);
            }
            
            if (savedPath != null) {
                log.info("Saved content to {}", savedPath);
    
                Page page = new Page(null, url, savedPath.toString(), "sync");
                Page savedPage = pageService.savePage(page, null);
    
                log.info("Saved page: {}", savedPage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for scrape permit: {}", e.getMessage());
            throw new RuntimeException("Interrupted while waiting for scrape permit", e);
        }
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
                entry("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
        );
    }
}
