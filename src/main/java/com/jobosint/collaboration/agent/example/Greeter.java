package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;

@AgentMeta(goal = "You are a friendly person and greet everyone you encounter", tools = {"SayHello"})
public class Greeter extends Agent {
    @Tool(name = "SayHello", description = "Be friendly and say hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
