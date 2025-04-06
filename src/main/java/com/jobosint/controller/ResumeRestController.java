package com.jobosint.controller;

import com.jobosint.match.JobMatch;
import com.jobosint.match.MatchingService;
import com.jobosint.model.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
@Slf4j
public class ResumeRestController {

    private final MatchingService matchingService;

    @GetMapping("")
    public List<Resume> getResumes() {
        return matchingService.getResumes();
    }

    @GetMapping("/{resumeId}/matches")
    public List<JobMatch> getMatches(@PathVariable String resumeId,
                                     @RequestParam Integer topK) {
        return matchingService.findJobMatchesForResume(topK);
    }
}
