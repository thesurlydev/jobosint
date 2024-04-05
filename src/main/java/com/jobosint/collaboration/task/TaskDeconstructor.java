package com.jobosint.collaboration.task;

import com.jobosint.service.NlpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TaskDeconstructor {

    private final NlpService nlpService;

    public DeconstructedTask deconstructTask(Task task) {
        List<String> subtasks = nlpService.getSubtasks(task.getDescription());
        return new DeconstructedTask(task.getDescription(), subtasks);
    }
}
