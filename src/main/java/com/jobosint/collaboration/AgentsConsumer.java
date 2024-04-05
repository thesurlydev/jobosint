package com.jobosint.collaboration;

import com.jobosint.collaboration.agent.AgentService;

import java.util.Set;

public interface AgentsConsumer {
    void setAgents(Set<AgentService> agents);
}
