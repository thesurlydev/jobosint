package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.tool.Tool;
import com.jobosint.model.GoogleSearchRequest;
import com.jobosint.model.GoogleSearchResponse;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.service.GoogleSearchService;
import com.jobosint.service.ScrapeService;
import lombok.RequiredArgsConstructor;

@Agent(goal = "Search the internet for relevant information about a topic")
@RequiredArgsConstructor
public class WebResearcher extends AgentService {
    private final GoogleSearchService googleSearchService;
    private final ScrapeService scrapeService;

    @Tool(name = "WebScraper", description = "Given a web URL scrape the HTML content. Only use this tool if a host name or URL is provided. Always include the full URL including the query string.")
    public ScrapeResponse scrape(String url) {
        return scrapeService.scrapeHtml(url);
    }

    @Tool(name = "PDFDownloader", description = "Given a web URL download the HTML content as a PDF file")
    public ScrapeResponse downloadAsPdf(String url) {
        return scrapeService.downloadAsPdf(url);
    }

    @Tool(name = "HARDownloader", description = "Given a web URL download the HAR file")
    public ScrapeResponse downloadAsHar(String url) {
        return scrapeService.downloadAsHar(url);
    }

    @Tool(name = "ScreenshotTool", description = "Given a web URL download the HAR file")
    public ScrapeResponse downloadAsScreenshot(String url) {
        return scrapeService.downloadAsScreenshot(url);
    }

    @Tool(name = "WebSearch", description = "Search the web for the given term")
    public GoogleSearchResponse search(GoogleSearchRequest searchRequest) {
        return googleSearchService.search(searchRequest);
    }
}
