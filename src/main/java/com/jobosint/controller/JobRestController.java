package com.jobosint.controller;

import com.jobosint.model.JobDetail;
import com.jobosint.model.ext.Page;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Slf4j
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

    @PostMapping("/ext")
    public void postJobFromExtension(@RequestBody Page page) {
        log.info("post from extension; url: {}", page.url());
    }
}
