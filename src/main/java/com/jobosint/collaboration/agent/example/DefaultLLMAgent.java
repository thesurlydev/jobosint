package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.agent.Agent;

@Agent(goal = "Be helpful by accomplishing a wide variety of tasks and answering questions")
public class DefaultLLMAgent extends AgentService {
    // Don't add tools to ensure just the LLM accomplishes the task
}
