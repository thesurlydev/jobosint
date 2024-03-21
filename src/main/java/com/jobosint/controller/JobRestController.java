package com.jobosint.controller;

import com.jobosint.model.JobDetail;
import com.jobosint.service.JobService;
import com.jobosint.service.ai.JobDescriptionParserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Slf4j
public class JobRestController {

    private final JobService jobService;
    private final JobDescriptionParserService jobDescriptionParserService;

    @GetMapping()
    @Operation(summary = "Get all jobs")
    public Iterable<JobDetail> getJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job by its id")
    public Optional<JobDetail> getJobDetail(@PathVariable UUID id) {
        return jobService.getJobDetail(id);
    }

}
