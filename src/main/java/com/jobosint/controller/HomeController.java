package com.jobosint.controller;

import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobRepository jobRepository;

    @GetMapping("/")
    public String home(Model model) {
        var jobs = jobRepository.findAllJobDetailOrderByCreatedAt();
        model.addAttribute("jobs", jobs);
        return "index";
    }
}
