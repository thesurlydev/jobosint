package com.jobosint.controller;

import com.jobosint.model.JobAndCompany;
import com.jobosint.model.JobDetail;
import com.jobosint.model.Note;
import com.jobosint.repository.JobRepository;
import com.jobosint.repository.NoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobRepository jobRepository;
    private final NoteRepository noteRepository;

    public JobRestController(JobRepository jobRepository, NoteRepository noteRepository) {
        this.jobRepository = jobRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping()
    public Iterable<JobAndCompany> getJobs() {
        return jobRepository.findAllJobAndCompany();
    }

    @GetMapping("/{id}")
    public JobDetail getJobs(@PathVariable UUID id) {
        var jobAndCompany = jobRepository.findJobDetailbyId(id);
        List<Note> notes = noteRepository.findByJobId(id);
        return new JobDetail(jobAndCompany, notes);
    }
}
