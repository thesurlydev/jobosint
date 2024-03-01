package com.jobosint.service;

import com.jobosint.model.greenhouse.Job;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;

@Disabled
public class GreenhouseServiceTest {

    private final GreenhouseService greenhouseService = new GreenhouseService();

    @Test
    public void downloadJobs() {
        try {
            greenhouseService.downloadJobs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getJobList() {
        greenhouseService.getJobList("gitlab");
    }

    @Test
    public void getJob() {
        Job job = greenhouseService.getJob("gitlab", "7096222002");
        System.out.println(job);

    }
}
