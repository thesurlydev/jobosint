package com.jobosint.controller;

import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;

    @GetMapping("/")
    public String home(Model model) {
        var jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "index";
    }
}
