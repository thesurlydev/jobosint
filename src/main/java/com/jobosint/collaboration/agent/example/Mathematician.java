package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.agent.example.model.SumRequest;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;

@AgentMeta(goal = "Answer mathematical questions and solve problems", tools = "AddingMachine")
public class Mathematician extends Agent {
    @Tool(name = "AddingMachine", description = "Add a list of numbers together")
    public int sum(SumRequest sumRequest) {
        return sumRequest.numbers().stream().mapToInt(Integer::intValue).sum();
    }
}
