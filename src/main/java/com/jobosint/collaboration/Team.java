package com.jobosint.collaboration;

import com.jobosint.collaboration.task.AgentTaskExecutor;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Team {

    private final AgentTaskExecutor agentTaskExecutor;

    private final List<Task> tasks = new ArrayList<>();

    public void clearTasks() {
        tasks.clear();
    }

    public Team addTasks(List<Task> tasks) {
        clearTasks();
        this.tasks.addAll(tasks);
        return this;
    }

    public List<TaskResult> kickoff() {
        return tasks.stream()
                .map(agentTaskExecutor::processTask)
                .toList();
    }
}
