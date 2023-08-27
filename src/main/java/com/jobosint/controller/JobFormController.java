package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import com.jobosint.model.form.JobForm;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import com.jobosint.repository.NotesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class JobFormController {
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    private final NotesRepository notesRepository;

    public JobFormController(JobRepository jobRepository, CompanyRepository companyRepository, NotesRepository notesRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.notesRepository = notesRepository;
    }

    @GetMapping("/job")
    public String jobForm(Model model) {
        model.addAttribute("job", new JobForm());

        Iterable<Company> companies = companyRepository.findAll();
        var companyList = new ArrayList<Company>();
        companies.forEach(companyList::add);
        companyList.sort(Comparator.comparing(Company::name));
        model.addAttribute("companies", companyList);

        return "/jobForm";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable UUID id, Model model) {

        var jobAndCompany = jobRepository.findJobDetailbyId(id);
        var notes = notesRepository.findByJobId(id);
        var jobDetail = new JobDetail(jobAndCompany, notes);

        model.addAttribute("job", jobDetail);

        return "/jobDetail";
    }

    @PostMapping("/job")
    public RedirectView jobSubmit(@ModelAttribute JobForm jobForm, Model model) {

        var job = new Job(null, jobForm.getCompanyId(), jobForm.getTitle(), jobForm.getUrl());
        jobRepository.save(job);

        return new RedirectView("/");
    }
}
