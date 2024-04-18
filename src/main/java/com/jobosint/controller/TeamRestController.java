package com.jobosint.controller;

import com.jobosint.collaboration.Team;
import com.jobosint.collaboration.agent.AgentRegistry;
import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crew")
@Slf4j
public class TeamRestController {

    private final Team team;
    private final AgentRegistry agentRegistry;

    @GetMapping()
    public TaskResult team(@RequestParam String task) {
        log.info("Given task: {}", task);
        List<TaskResult> taskResults = team
                .addTasks(List.of(new Task(task)))
                .kickoff();
        TaskResult taskResult = taskResults.getFirst();
        log.info("Task result:\n\n {}\n\n", taskResult);
        return taskResult;
    }

    @GetMapping("/agents")
    public List<AgentService> agents() {
        var agents = agentRegistry.allAgents();
        return agents.values().stream().toList();
    }
}
