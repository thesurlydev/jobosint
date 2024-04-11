package com.jobosint.integration.greenhouse.service;

import com.jobosint.integration.greenhouse.model.GetJobResult;
import com.jobosint.integration.greenhouse.model.GreenhouseJobsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class GreenhouseServiceTest {

    @Autowired
    private GreenhouseService greenhouseService;

    @Test
    public void fetchJobs() {
        try {
            greenhouseService.fetchJobs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getJobList() {
        GreenhouseJobsResponse greenhouseJobsResponse = greenhouseService.getJobList("gitlab");
        System.out.println(greenhouseJobsResponse);
    }

    @Test
    public void getJob() {
        GetJobResult result = greenhouseService.getJob("gitlab", "7096222002");
        System.out.println(result);
    }
}
