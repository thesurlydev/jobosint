package com.jobosint.collaboration;

import com.jobosint.collaboration.agent.example.BusinessAnalyst;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.model.GoogleSearchResponse;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.model.ai.CompanyDetail;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CrewTest {

    @Autowired
    ChatClient chatClient;
    @Autowired
    Crew crew;

    @Autowired
    BusinessAnalyst businessAnalyst;

    /*@Test
    public void testToolAnnotations() {
        String[] tools = businessAnalyst.getTools();
        assertEquals(1, tools.length);
    }*/

    @Test
    public void sumTask() {
        var task = new Task("Add the numbers 1, 2, 3, 4, 5");
        TaskResult result = crew.processTask(chatClient, task);
        assertInstanceOf(Integer.class, result.getData());
    }

    @Test
    public void webSearchTask() {
        var task = new Task("What are the top 5 search results for Java?");
        TaskResult result = crew.processTask(chatClient, task);
        assertInstanceOf(GoogleSearchResponse.class, result.getData());
    }

    @Test
    public void companyDetailTask() {
        var task = new Task("Give me information about Alphabet");
        TaskResult result = crew.processTask(chatClient, task);
        assertInstanceOf(CompanyDetail.class, result.getData());
    }

    @Test
    public void scrapeTask() {
        var task = new Task("Get the content of the page at https://surly.dev");
        TaskResult result = crew.processTask(chatClient, task);
        assertInstanceOf(ScrapeResponse.class, result.getData());
    }

    @Test
    public void chooseAgentTest() {
        var task = new Task("Say hello");
        Optional<String> agent = crew.chooseAgent(chatClient, task);
        assertTrue(agent.isPresent());
        assertEquals("Greeter", agent.get());
    }

    @Test
    public void chooseAgentsTest() {
        var tasks = Lists.list(
                new Task("Say hello"),
                new Task("who is joe biden"),
                new Task("Add the numbers 1, 2, 3, 4, 5")
        );
        Map<String, Object> assignments = crew.chooseAgents(chatClient, tasks);
        // print the assignments
        assignments.forEach((k, v) -> System.out.println(k + " -> " + v));
        assertEquals(tasks.size(), assignments.size());
    }

    @Test
    public void sayHelloTask() {
        var task = new Task("Say hello");
        TaskResult result = crew.processTask(chatClient, task);
        assertEquals("Hello, World!", result.getData());
    }


}
