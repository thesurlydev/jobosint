package com.jobosint.controller;

import com.jobosint.model.JobAndCompany;
import com.jobosint.repository.JobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/jobs")
    public Iterable<JobAndCompany> getJobs() {
        return jobRepository.findAllJobAndCompany();
    }
}
