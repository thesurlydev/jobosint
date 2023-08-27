package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import com.jobosint.model.Note;
import com.jobosint.model.form.JobForm;
import com.jobosint.model.form.NoteForm;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import com.jobosint.repository.NoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class JobFormController {
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    private final NoteRepository noteRepository;

    public JobFormController(JobRepository jobRepository, CompanyRepository companyRepository, NoteRepository noteRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/job")
    public String jobForm(Model model) {
        model.addAttribute("job", new JobForm());

        Iterable<Company> companies = companyRepository.findAll();
        var companyList = new ArrayList<Company>();
        companies.forEach(companyList::add);
        companyList.sort(Comparator.comparing(Company::name));
        model.addAttribute("companies", companyList);

        var sources = List.of("LinkedIn", "Google Jobs", "Indeed", "Recruiter", "Company Job Site");
        model.addAttribute("sources", sources);

        return "/jobForm";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable UUID id, Model model) {

        var jobAndCompany = jobRepository.findJobDetailbyId(id);
        var notes = noteRepository.findByJobId(id);
        var jobDetail = new JobDetail(jobAndCompany, notes);
        model.addAttribute("job", jobDetail);

        var noteForm = new NoteForm(id);
        model.addAttribute("note", noteForm);

        return "/jobDetail";
    }

    @PostMapping("/job")
    public RedirectView jobSubmit(@ModelAttribute JobForm jobForm) {

        var job = new Job(null, jobForm.getCompanyId(), jobForm.getTitle(), jobForm.getUrl(), null, null, jobForm.getSalaryMin(), jobForm.getSalaryMax(), jobForm.getSource());
        var savedJob = jobRepository.save(job);

        if (!jobForm.getNotes().isEmpty()) {
            var notes = new Note(null, savedJob.id(), jobForm.getNotes(), LocalDateTime.now());
            noteRepository.save(notes);
        }

        return new RedirectView("/");
    }
}
