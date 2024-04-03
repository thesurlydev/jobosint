package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;
import org.springframework.stereotype.Component;

@AgentMeta(
        goal = "Search the internet for relevant information about a topic",
        disabled = true,
        tools = {"WebScraper", "WebSearch"}
)
public class WebResearcher extends Agent {
    public WebResearcher() {
    }
}
