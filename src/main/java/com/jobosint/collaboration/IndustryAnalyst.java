package com.jobosint.collaboration;

import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;
import org.springframework.stereotype.Component;

@AgentMeta(goal = """
        Research, analyze, and provide insights into a specific industry sector.
        Assess market trends, industry performance, competitive landscapes, and technological advancements.
        """, tools = {"GetCompanyDetail", "SayHello"})
@Component
public class IndustryAnalyst extends Agent {

    @Tool(name = "SayHello", description = "Be friendly and say hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
