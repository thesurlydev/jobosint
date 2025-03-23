package com.jobosint.controller;

import com.jobosint.ingest.IngestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ingest")
@Slf4j
public class IngestController {

    private final IngestService ingestService;

    @GetMapping("/resume")
    @Operation(summary = "Ingest resume")
    public void ingest() {
        ingestService.ingestResume("file:///Users/shane/projects/jobosint/data/resumes/shane-witbeck-2025.pdf");
    }
}
