package com.jobosint.controller;

import com.jobosint.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/applications")
    public String applications(Model model) {
        var apps = applicationService.getAllApplications();
        model.addAttribute("apps", apps);
        return "applications";
    }
}
