package com.jobosint.controller;

import com.jobosint.model.JobDetail;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobService jobService;

    @GetMapping()
    public Iterable<JobDetail> getJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public JobDetail getJobDetail(@PathVariable UUID id) {
        return jobService.getJobDetail(id);
    }
}
