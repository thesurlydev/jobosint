package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.form.JobForm;
import com.jobosint.service.AttributeService;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class JobFormController {

    private final AttributeService attributeService;
    private final JobService jobService;
    private final CompanyService companyService;

    @GetMapping("/job")
    public String jobForm(Model model) {
        prepareJobForm(model);
        model.addAttribute("job", new JobForm());
        return "/jobForm";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable UUID id, Model model) {
        var jobDetail = jobService.getJobDetail(id);
        jobDetail.ifPresent(value -> model.addAttribute("detail", value));
        return "/jobDetail";
    }

    @GetMapping("/jobs/{id}/delete")
    public RedirectView deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return new RedirectView("/");
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable UUID id, Model model) {
        var maybeJob = jobService.getJob(id);
        maybeJob.ifPresent(job -> {
            var jobForm = new JobForm(job);
            model.addAttribute("job", jobForm);
        });
        prepareJobForm(model);
        return "/jobForm";
    }

    private void prepareJobForm(Model model) {
        List<Company> companies = companyService.getAllSorted();
        model.addAttribute("companies", companies);

        var sources = attributeService.getSources();
        model.addAttribute("sources", sources);
    }

    @PostMapping("/job")
    public RedirectView jobSubmit(@ModelAttribute JobForm jobForm) {
        var job = Job.fromForm(jobForm);
        jobService.saveJob(job);
        return new RedirectView("/");
    }
}
