package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.tool.Tool;
import com.jobosint.model.ScrapeRequest;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.model.SelectAttribute;
import com.jobosint.service.ScrapeService;
import lombok.RequiredArgsConstructor;

@Agent(goal = "Extract links from a given webpage")
@RequiredArgsConstructor
public class LinkExtractor extends AgentService {

    private final ScrapeService scrapeService;

    @Tool(name = "ExtractLinks", description = "Extract links from a given webpage")
    public ScrapeResponse extractLinks(String url) {
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        var req = new ScrapeRequest(url, "a", SelectAttribute.attr, "href", null);
        return scrapeService.scrape(req);
    }
}
