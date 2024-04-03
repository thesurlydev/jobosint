package com.jobosint.controller;

import com.jobosint.collaboration.Crew;
import com.jobosint.collaboration.Task;
import com.jobosint.collaboration.task.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@Slf4j
public class CrewController {

    private final ChatClient chatClient;
    private final Crew crew;

    @GetMapping("/crew")
    public String crew(@RequestParam String task) {
        log.info("Given task: {}", task);
        TaskResult taskResult = crew.processTask(chatClient, new Task(task));
        log.info("Task result:\n\n {}", taskResult);
        return "crew";
    }

    /*@GetMapping("/crew/breakdown")
    public String breakDown(@RequestParam String task) {
        log.info("Given task: {}", task);
        TaskList taskList = crew.breakdownTask(chatClient, new Task(task));
        taskList.subtasks().forEach(t -> log.info("Subtask: {}", t));
        return "crew";
    }*/
}
