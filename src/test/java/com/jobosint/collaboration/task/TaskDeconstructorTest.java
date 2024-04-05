package com.jobosint.collaboration.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TaskDeconstructorTest {

    @Autowired
    TaskDeconstructor taskDeconstructor;

    @Test
    public void testDeconstructTask_No_Subtasks() {
        var task = new Task("Give me information about Alphabet");
        var deconstructedTask = taskDeconstructor.deconstructTask(task);
        assertEquals("Give me information about Alphabet", deconstructedTask.originalTask());
        assertTrue(deconstructedTask.subtasks().isEmpty());
    }

    @Test
    public void testDeconstructTask_Subtasks() {
        var task = new Task("Search for the top 5 results for Java, then scrape each page");
        var deconstructedTask = taskDeconstructor.deconstructTask(task);
        assertEquals("Search for the top 5 results for Java, then scrape each page", deconstructedTask.originalTask());
        assertEquals(2, deconstructedTask.subtasks().size());
        assertEquals("Search for the top 5 results for Java", deconstructedTask.subtasks().getFirst());
        assertEquals("scrape each page", deconstructedTask.subtasks().get(1));
    }
}
