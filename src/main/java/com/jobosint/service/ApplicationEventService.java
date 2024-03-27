package com.jobosint.service;

import com.jobosint.model.ApplicationEvent;
import com.jobosint.repository.ApplicationEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationEventService {
    private final ApplicationEventRepository applicationEventRepository;

    public List<ApplicationEvent> findAll() {
        return applicationEventRepository.findAll();
    }

    public ApplicationEvent save(ApplicationEvent applicationEvent) {
        return applicationEventRepository.save(applicationEvent);
    }
}
