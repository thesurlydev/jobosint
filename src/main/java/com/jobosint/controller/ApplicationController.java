package com.jobosint.controller;

import com.jobosint.model.Application;
import com.jobosint.model.form.ApplicationForm;
import com.jobosint.service.ApplicationService;
import com.jobosint.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ApplicationController {
    private final AttributeService attributeService;
    private final ApplicationService applicationService;

    @GetMapping("/apps")
    public String applications(Model model) {
        var apps = applicationService.getAllApplications();
        model.addAttribute("apps", apps);
        model.addAttribute("numApps", apps.size());
        return "applications";
    }

    @GetMapping("/app")
    public String addApp(Model model) {
        prepareAppForm(model);
        model.addAttribute("app", new ApplicationForm());
        return "applicationForm";
    }

    @GetMapping("/apps/{id}/edit")
    public String editApp(@PathVariable UUID id, Model model) {
        var appDetail = applicationService.getApplicationDetail(id);
        var appForm = new ApplicationForm(appDetail);
        model.addAttribute("app", appForm);
        prepareAppForm(model);
        return "applicationForm";
    }

    @PostMapping("/app")
    public RedirectView appSubmit(@ModelAttribute ApplicationForm appForm) {
        var app = Application.fromForm(appForm);
        applicationService.saveApplication(app);
        return new RedirectView("/apps");
    }

    private void prepareAppForm(Model model) {
        var statuses = attributeService.getApplicationStatuses();
        model.addAttribute("statuses", statuses);

        var resumes = attributeService.getResumes();
        model.addAttribute("resumes", resumes);
    }
}
