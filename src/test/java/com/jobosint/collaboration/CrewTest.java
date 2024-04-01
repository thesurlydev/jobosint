package com.jobosint.collaboration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CrewTest {

    @Autowired
    Crew crew;

    @Autowired IndustryAnalyst industryAnalyst;

    @Test
    public void testToolAnnotations() {
        String[] tools = industryAnalyst.getTools();
        System.out.println(Arrays.toString(tools));
    }

    @Test
    public void chooseAgentTest() {
//        var task = new Task("What is the meaning of the word sanctum");
//        var task = new Task("What is the history of the word sanctum");
//        var task = new Task("Describe the current state of the AI industry and predict hot topics in the next year");
        var task = new Task("Say hello");
        Optional<String> agent = crew.chooseAgent(task);
        assertTrue(agent.isPresent());
        assertEquals("industryAnalyst", agent.get());
    }

    @Test
    public void chooseToolTest() {
//        var task = new Task("What is the meaning of the word sanctum");
//        var task = new Task("What is the history of the word sanctum");
//        var task = new Task("Describe the current state of the AI industry and predict hot topics in the next year");
        var task = new Task("Say hello");
        Optional<ToolMetadata> tool = industryAnalyst.chooseTool(task);
        assertTrue(tool.isPresent());
        assertEquals("SayHello", tool.get().name());
    }

    @Test
    public void processTaskTest() {
        var task = new Task("Say hello");
        Optional<String> result = industryAnalyst.processTask(task, String.class);
        assertTrue(result.isPresent());
        assertEquals("Hello, World!", result.get());
    }

    @Test
    public void hubTest() {
        Map<String, Agent> agentNames = crew.agents();
        System.out.println(agentNames);
    }
}
