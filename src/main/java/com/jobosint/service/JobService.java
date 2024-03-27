package com.jobosint.service;

import com.jobosint.event.CompanyCreatedEvent;
import com.jobosint.event.JobCreatedEvent;
import com.jobosint.model.Job;
import com.jobosint.model.JobAttribute;
import com.jobosint.model.JobDetail;
import com.jobosint.repository.ApplicationRepository;
import com.jobosint.repository.JobAttributeRepository;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public List<JobDetail> getAllJobs() {
        return jobRepository.findAllJobDetailOrderByCreatedAt();
    }

    public Optional<JobDetail> getJobDetail(UUID id) {
        return jobRepository.findJobDetailbyId(id);
    }

    public Optional<Job> getJob(UUID id) {
        return jobRepository.findById(id);
    }

    public void deleteJob(UUID id) {
        getJob(id).ifPresent(job -> {
            applicationRepository.deleteAllByJobId(id);
            jobRepository.delete(job);
            log.info("Deleted job: {}", job);
        });
    }

    public Job saveJob(Job job) {
        Job savedJob = jobRepository.save(job);
        if (job.id() == null) {
            applicationEventPublisher.publishEvent(new JobCreatedEvent(this, savedJob));
        }
        return savedJob;
    }

    public List<JobDetail> searchJobs(String query) {
        return jobRepository.searchJobs(query);
    }
}
