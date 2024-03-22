package com.jobosint.service;

import com.jobosint.model.Application;
import com.jobosint.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public Iterable<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}
