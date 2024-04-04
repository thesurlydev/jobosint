package com.jobosint.collaboration.agent;

import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.agent.example.WebResearcher;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.model.ScrapeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebResearcherTest {

    @Autowired
    WebResearcher webResearcher;

    @Test
    public void testScrapeTask() throws NoSuchMethodException {
        var task = new Task("Get the content of the page at https://surly.dev");
        TaskResult result = webResearcher.processTask(task);

        assertEquals("ScrapeResponse", result.getDataType());
        assertInstanceOf(ScrapeResponse.class, result.getData());

        ScrapeResponse response = (ScrapeResponse) result.getData();
        assertEquals(1, response.data().size());
        assertTrue(response.data().iterator().next().contains("The Surly Dev"));
    }
}
