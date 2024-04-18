package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.agent.example.model.SumRequest;
import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.tool.Tool;

@Agent(goal = "Answer mathematical questions and solve problems")
public class Mathematician extends AgentService {
    @Tool(name = "AddingMachine", description = "Add a list of numbers together")
    public int sum(SumRequest sumRequest) {
        return sumRequest.numbers().stream().mapToInt(Integer::intValue).sum();
    }
}
