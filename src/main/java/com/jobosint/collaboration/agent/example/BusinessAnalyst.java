package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.annotation.Tool;

@Agent(
        goal = "Provide business analysis of companies",
        tools = {"GetCompanyDetail", "GetCompanyFocus"}
)
public class BusinessAnalyst extends AgentService {

    @Tool(name = "GetCompanyFocus", description = "Given the name of a company, return the focus of the company")
    public String getCompanyFocus(String companyName) {
        return "The focus of " + companyName + " is on providing software solutions";
    }

}
