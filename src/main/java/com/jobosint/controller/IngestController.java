package com.jobosint.controller;

import com.jobosint.ingest.IngestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ingest")
@Slf4j
public class IngestController {

    private final IngestService ingestService;

    @GetMapping("/resume")
    @Operation(summary = "Ingest resume")
    public ResponseEntity<String> ingest() {
        ingestService.ingestResume("file:///Users/shane/projects/jobosint/data/resumes/shane-witbeck-2025.pdf");
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Ingest resume")
    public ResponseEntity<String> ingest(@PathVariable UUID jobId) {
        ingestService.ingestJob(jobId);
        return ResponseEntity.ok("ok");
    }
}
