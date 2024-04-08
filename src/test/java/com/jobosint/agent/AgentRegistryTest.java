package com.jobosint.agent;

import com.jobosint.collaboration.agent.AgentRegistry;
import com.jobosint.collaboration.agent.AgentService;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AgentRegistryTest {

    @Test
    public void testEnabled() {

        Set<AgentService> agents = Set.of(
                new AgentService("agent1",  null, null, false),
                new AgentService("agent2", null, null, true)
        );

        AgentRegistry registry = new AgentRegistry();
        registry.setAgents(agents);

        Map<String, AgentService> enabledAgents = registry.enabledAgents();

        assertEquals(1, enabledAgents.size());
        assertFalse(enabledAgents.get("agent1").getDisabled());
    }
}
