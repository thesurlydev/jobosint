package com.jobosint.controller;

import com.jobosint.model.Application;
import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.form.JobForm;
import com.jobosint.service.ApplicationService;
import com.jobosint.service.AttributeService;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JobFormController {

    private final ApplicationService appService;
    private final AttributeService attributeService;
    private final JobService jobService;
    private final CompanyService companyService;

    @GetMapping("/jobs")
    public String home(Model model) {
        var jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "jobs";
    }

    @GetMapping("/job")
    public String jobForm(Model model) {
        prepareJobForm(model);
        var jobForm = new JobForm();
        jobForm.setStatus("Active");
        jobForm.setSource("LinkedIn");
        model.addAttribute("job", jobForm);
        return "/jobForm";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable UUID id, Model model) {
        var jobDetail = jobService.getJobDetail(id);
        if (jobDetail.isPresent()) {
            model.addAttribute("detail", jobDetail.get());
        } else {
            log.warn("Job not found: {}", id);
        }
        return "jobDetail";
    }

    @GetMapping("/jobs/{id}/delete")
    public RedirectView deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return new RedirectView("/");
    }

    @GetMapping("/jobs/{id}/apply")
    public RedirectView apply(@PathVariable UUID id) {

        // get the job
        var maybeJobDetail = jobService.getJobDetail(id);

        if (maybeJobDetail.isPresent()) {

            var jobDetail = maybeJobDetail.get();
            var job = jobDetail.job();

            // create an application from the job
            var app = Application.fromJob(maybeJobDetail.get());

            // save the application
            var savedApp = appService.saveApplication(app);

            // update job status to "Applied"
            var updatedJob = Job.fromJobWithNewStatus(job, "Applied");
            jobService.saveJob(updatedJob);

            // redirect to app detail view so we can click through the apply link
            return new RedirectView("/apps/" + savedApp.id() + "/edit");

        } else {
            log.warn("Job not found: {}", id);
        }

        return new RedirectView("/apps");
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable UUID id, Model model) {
        var maybeJob = jobService.getJob(id);
        maybeJob.ifPresent(job -> {
            var jobForm = new JobForm(job);
            model.addAttribute("job", jobForm);
        });
        prepareJobForm(model);
        return "jobForm";
    }

    private void prepareJobForm(Model model) {
        List<Company> companies = companyService.getAllSorted();
        model.addAttribute("companies", companies);

        var sources = attributeService.getSources();
        model.addAttribute("sources", sources);

        var statuses = attributeService.getJobStatuses();
        model.addAttribute("statuses", statuses);
    }

    @PostMapping("/job")
    public RedirectView jobSubmit(@ModelAttribute JobForm jobForm) {
        var job = Job.fromForm(jobForm);
        jobService.saveJob(job);
        return new RedirectView("jobs");
    }
}
