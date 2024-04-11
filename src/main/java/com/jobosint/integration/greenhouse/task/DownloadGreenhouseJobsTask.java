package com.jobosint.integration.greenhouse.task;

import com.jobosint.integration.greenhouse.service.GreenhouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class DownloadGreenhouseJobsTask {
    private final GreenhouseService greenhouseService;

    @Scheduled(fixedRate = 600000, initialDelay = 5000)
    public void fetchJobs() throws IOException {
        greenhouseService.fetchJobs();
    }
}
