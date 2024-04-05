package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.annotation.Tool;
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

    @Tool(name = "WebScraper", description = "Given a web URL scrape the HTML content. Only use this tool if a host name or URL is provided.")
    public ScrapeResponse scrape(String url) {
        return scrapeService.scrapeHtml(url);
    }

    @Tool(name = "WebSearch", description = "Search the web for the given term")
    public GoogleSearchResponse search(GoogleSearchRequest searchRequest) {
        return googleSearchService.search(searchRequest);
    }
}
