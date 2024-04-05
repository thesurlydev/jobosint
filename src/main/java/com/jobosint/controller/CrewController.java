package com.jobosint.controller;

import com.jobosint.collaboration.Crew;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.model.form.ContactForm;
import com.jobosint.model.form.CrewForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CrewController {
    private final ChatClient chatClient;
    private final Crew crew;

    @GetMapping("/crew")
    public String crew(Model model) {
        model.addAttribute("crewForm", new CrewForm());
        return "crew";
    }

    @PostMapping("/crew")
    public String processTask(@ModelAttribute CrewForm crewForm, Model model) {
        log.info("Given task: {}", crewForm);
        var task = new Task(crewForm.getTask());
        TaskResult taskResult = crew.processTask(chatClient, task);
        model.addAttribute("taskResult", taskResult);
        return "crew";
    }
}
