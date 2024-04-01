package com.jobosint.collaboration;

import com.jobosint.collaboration.annotation.AgentMeta;

@AgentMeta(goal = "Search the internet for relevant information about a topic",
        tools = "WebScraper")
public class WebResearcher extends Agent {
}
