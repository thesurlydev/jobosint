package com.jobosint.controller;

import com.jobosint.collaboration.Team;
import com.jobosint.collaboration.agent.AgentRegistry;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.model.form.TeamForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final Team team;
    private final AgentRegistry agentRegistry;

    @GetMapping("/team")
    public String team(Model model) {
        model.addAttribute("teamForm", new TeamForm());
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "team";
    }

    @PostMapping("/team")
    public String processTask(@ModelAttribute TeamForm teamForm, Model model) {
        log.info("Given task: {}", teamForm);
        Task task = teamForm.toTask();
        List<TaskResult> taskResults = team
                .addTasks(List.of(task))
                .kickoff();

        TaskResult taskResult = taskResults.getFirst();
        model.addAttribute("taskResult", taskResult);
        model.addAttribute("agents", agentRegistry.enabledAgents());
        return "team";
    }
}
