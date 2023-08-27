package com.jobosint.controller;

import com.jobosint.repository.JobRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final JobRepository jobRepository;

    public HomeController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        var jobs = jobRepository.findAllJobAndCompany();
        model.addAttribute("jobs", jobs);

        return "index";
    }
}
