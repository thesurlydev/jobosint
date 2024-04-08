package com.jobosint.controller;

import com.jobosint.collaboration.Crew;
import com.jobosint.collaboration.agent.AgentRegistry;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.model.form.CrewForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CrewController {
    private final ChatClient chatClient;
    private final Crew crew;
    private final AgentRegistry agentRegistry;

    @GetMapping("/crew")
    public String crew(Model model) {
        model.addAttribute("crewForm", new CrewForm());
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "crew";
    }

    @PostMapping("/crew")
    public String processTask(@ModelAttribute CrewForm crewForm, Model model) {
        log.info("Given task: {}", crewForm);
        Task task;
        if (crewForm.getAgent() != null && !crewForm.getAgent().isEmpty()) {
            task = new Task(crewForm.getTask(), crewForm.getAgent());
        } else {
            task = new Task(crewForm.getTask());
        }
        List<TaskResult> taskResults = crew
                .addTasks(List.of(task))
                .kickoff();

        TaskResult taskResult = taskResults.getFirst();
        model.addAttribute("taskResult", taskResult);
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "crew";
    }
}
