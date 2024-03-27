package com.jobosint.service;

import com.jobosint.model.ApplicationEvent;
import com.jobosint.model.ApplicationEventDetail;
import com.jobosint.repository.ApplicationEventRepository;
import com.jobosint.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<ApplicationEventDetail> getApplicationEventDetailById(UUID id) {
        return applicationEventRepository.findApplicationEventDetailById(id);
    }

    public List<ApplicationEventDetail> getAllApplicationEventDetails() {
        return applicationEventRepository.findAllApplicationEventDetails();
    }

}
