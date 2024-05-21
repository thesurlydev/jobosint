package com.jobosint.controller;

import com.jobosint.integration.greenhouse.service.GreenhouseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {

    private final GreenhouseService greenhouseService;

    @GetMapping("/greenhouse")
    @Operation(summary = "Kick off a greenhouse job sync")
    public String greenhouseTask() {
        try {
            greenhouseService.fetchJobs();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching Greenhouse jobs", e);
        }
        return "ok";
    }
}
