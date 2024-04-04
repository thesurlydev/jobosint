package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;

@AgentMeta(goal = "Research, analyze, and provide insights into a specific industry sector. Assess market trends, industry performance, competitive landscapes, and technological advancements.",
        tools = {"GetCompanyDetail"})
public class IndustryAnalyst extends Agent {


}
