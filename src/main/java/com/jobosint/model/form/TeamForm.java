package com.jobosint.model.form;

import com.jobosint.collaboration.task.Task;
import lombok.Data;

@Data
public class TeamForm {
    private String task;
    private String agent;
    
    public Task toTask() {
        if (agent != null && !agent.isEmpty()) {
            return new Task(task, agent);
        } else {
            return new Task(task);
        }
    }
}
