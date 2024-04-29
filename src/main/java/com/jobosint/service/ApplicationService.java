package com.jobosint.service;

import com.jobosint.model.Application;
import com.jobosint.model.ApplicationDetail;
import com.jobosint.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public Optional<Application> getApplication(UUID id) {
        return applicationRepository.findById(id);
    }

    public ApplicationDetail getApplicationDetail(UUID id) {
        return applicationRepository.findApplicationDetailById(id);
    }

    public List<ApplicationDetail> getAllApplications() {
        return applicationRepository.findAllApplicationDetail();
    }

    public List<ApplicationDetail> getAllApplicationsSortedByCompany() {
        return applicationRepository.findAllApplicationDetailSortByCompany();
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }
}
