package com.jobosint.integration.linkedin.task;

import com.jobosint.integration.greenhouse.config.GreenhouseConfig;
import com.jobosint.integration.linkedin.config.LinkedInConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class CheckJobStatusTask {
    private final LinkedInConfig linkedInConfig;

    @Scheduled(cron="#{greenhouseConfig.fetchJobsCron}")
    public void fetchJobs() throws IOException {

    }
}
