package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;

@Agent(
        goal = "Search the internet for relevant information about a topic",
        tools = {"WebScraper", "WebSearch"}
)
public class WebResearcher extends AgentService {
}
