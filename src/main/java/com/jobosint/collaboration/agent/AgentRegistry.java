package com.jobosint.collaboration.agent;

import com.jobosint.collaboration.AgentsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class AgentRegistry implements AgentsConsumer {

    private final Map<String, AgentService> allAgents = new HashMap<>();
    private final Map<String, AgentService> enabledAgents = new HashMap<>();

    private void registerAgent(AgentService agent) {
        if (!agent.getDisabled()) {
            enabledAgents.put(agent.getName(), agent);
        }
        allAgents.put(agent.getName(), agent);
    }

    @Override
    public void setAgents(Set<AgentService> agents) {
        agents.forEach(this::registerAgent);
    }

    public AgentService getAgent(String agentName) {
        return allAgents.get(agentName);
    }

    public Map<String, AgentService> allAgents() {
        return allAgents;
    }

    public Map<String, AgentService> enabledAgents() {
        return enabledAgents;
    }
}
