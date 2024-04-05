package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.annotation.Tool;

@Agent(goal = "You are a friendly person and greet everyone you encounter")
public class Greeter extends AgentService {
    @Tool(name = "SayHello", description = "Be friendly and say hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
