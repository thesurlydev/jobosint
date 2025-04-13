package com.jobosint.controller;

import com.jobosint.model.JobDetail;
import com.jobosint.model.LinkedInJobSearchRequest;
import com.jobosint.service.LinkedInService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/linkedin")
@RestController
@RequiredArgsConstructor
public class LinkedInRestController {

    private final LinkedInService linkedInService;

    @GetMapping("/sync")
    @Operation(summary = "Sync LinkedIn jobs based on the given search request")
    public ResponseEntity<String> syncJobs() {
        linkedInService.searchJobs(new LinkedInJobSearchRequest("java", 50));
        return ResponseEntity.ok("ok");
    }
}
