package com.jobosint.service;

import com.jobosint.collaboration.tool.Tool;
import com.jobosint.model.GoogleSearchRequest;
import com.jobosint.model.GoogleSearchResponse;
import com.jobosint.model.GoogleSearchResult;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoogleSearchService {

    private final Browser browser;

    @Tool(name = "WebSearch", description = "Search the web for the given term")
    public GoogleSearchResponse search(GoogleSearchRequest searchRequest) {

        String term = searchRequest.query();
        int maxResults = searchRequest.maxResults();

        List<GoogleSearchResult> allResults = new ArrayList<>();
        long scrollSleepInterval = 200L;
        double scrollAmount = 2000.0;
        int maxScrollCount = 100;

        Page page = browser.newPage();
        page.navigate("https://www.google.com/search?q=" + term);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        Set<GoogleSearchResult> results = new HashSet<>();
        int scrollCount = 0;
        int prevAllResultsSize = 0;
        boolean keepScrolling = true;
        int noNewResultsCount = 1;

        while (keepScrolling && (scrollCount < maxScrollCount) && (allResults.size() < maxResults)) {
            page.mouse().wheel(0.0, scrollAmount);
            scrollCount++;
            System.out.println("scrollCount: " + scrollCount);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            ElementHandle moreResultsLink = null;

            for (ElementHandle link : page.querySelectorAll("a")) {
                String href = link.getAttribute("href");
                if (href == null) continue;
                if (href.startsWith("/")) {
                    String ariaLabel = link.getAttribute("aria-label");
                    if (href.startsWith("/search?q=" + term) && "More results".equals(ariaLabel) && link.isVisible()) {
                        System.out.println("found more results link: " + href);
                        moreResultsLink = link;
                    } else {
                        continue;
                    }
                }
                if (href.equals("#")) continue;
                if (href.startsWith("https://www.youtube.com") && href.contains("&")) {
                    continue;
                }
                if (href.contains("google.com")) {
                    continue;
                }

                if (!href.startsWith("/search")) {
                    String text = link.innerText() != null ? link.innerText() : href;
                    GoogleSearchResult result = new GoogleSearchResult(href, text);
                    results.add(result);
                }
            }

            if (results.isEmpty()) {
                System.out.println("no links found");
                throw new RuntimeException("no links found");
            }

            if (moreResultsLink != null) {
                System.out.println("clicking more results link");
                moreResultsLink.click();
            }

            allResults.addAll(results);

            if (prevAllResultsSize == allResults.size()) {
                System.out.println("no new results count: " + noNewResultsCount);
                noNewResultsCount++;
                if (noNewResultsCount > 3) {
                    keepScrolling = false;
                }
            } else {
                noNewResultsCount = 1;
            }
            prevAllResultsSize = allResults.size();

            if (keepScrolling) {
                try {
                    Thread.sleep(scrollSleepInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }

        if (allResults.size() > maxResults) {
            allResults = allResults.stream().limit(maxResults).collect(Collectors.toList());
        }

        return new GoogleSearchResponse(allResults);
    }
}
