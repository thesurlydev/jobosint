package com.jobosint.service;

import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<JobDetail> getAllJobs() {
        return jobRepository.findAllJobDetailOrderByCreatedAt();
    }

    public JobDetail getJobDetail(UUID id) {
        return jobRepository.findJobDetailbyId(id);
    }

    public Optional<Job> getJob(UUID id) {
        return jobRepository.findById(id);
    }

    public void deleteJob(UUID id) {
        jobRepository.deleteById(id);
    }

    public void saveJob(Job job) {
        var savedJob = jobRepository.save(job);
    }
}
