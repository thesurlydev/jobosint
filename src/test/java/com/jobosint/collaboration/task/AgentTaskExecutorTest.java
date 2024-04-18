package com.jobosint.collaboration.task;

import com.jobosint.model.GoogleSearchResponse;
import com.jobosint.model.ScrapeResponse;
import com.jobosint.model.ai.CompanyDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
public class AgentTaskExecutorTest {

    @Autowired
    AgentTaskExecutor agentTaskExecutor;


    @Test
    public void sumTask() {
        var task = new Task("Add the numbers 1, 2, 3, 4, 5");
        TaskResult result = agentTaskExecutor.processTask(task);
        assertInstanceOf(Integer.class, result.getData());
    }

    @Test
    public void webSearchTask() {
        var task = new Task("What are the top 5 search results for Java?");
        TaskResult result = agentTaskExecutor.processTask(task);
        assertInstanceOf(GoogleSearchResponse.class, result.getData());
    }

    @Test
    public void companyDetailTask() {
        var task = new Task("Give me information about Alphabet");
        TaskResult result = agentTaskExecutor.processTask(task);
        assertInstanceOf(CompanyDetail.class, result.getData());
    }

    @Test
    public void scrapeTask() {
        var task = new Task("Get the content of the page at https://surly.dev");
        TaskResult result = agentTaskExecutor.processTask(task);
        assertInstanceOf(ScrapeResponse.class, result.getData());
    }

    @Test
    public void sayHelloTask() {
        var task = new Task("Say hello");
        TaskResult result = agentTaskExecutor.processTask(task);
        assertEquals("Hello, World!", result.getData());
    }


}
