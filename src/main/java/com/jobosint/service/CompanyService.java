package com.jobosint.service;

import com.jobosint.event.CompanyCreatedEvent;
import com.jobosint.event.CompanyDeletedEvent;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public List<Company> search(String query) {
        if (query.startsWith("\"") && query.endsWith("\"")) {
            var exact = query.replaceAll("\"", "");
            log.info("Performing exact search for: {}", exact);
            return companyRepository.findCompaniesByNameIsIgnoreCase(exact);
        }
        var results = companyRepository.findCompaniesByNameIsIgnoreCase(query);
        if (results.size() == 1) {
            log.info("Found exact match");
            return results;
        }
        log.info("No exact match; performing 'like' search for: {}", query);
        return companyRepository.findCompaniesByNameContainingIgnoreCase(query);
    }

    @Transactional
    public Company saveCompany(Company company) {
        var persistedCompany = companyRepository.save(company);
        if (company.id() == null) {
            applicationEventPublisher.publishEvent(new CompanyCreatedEvent(this, persistedCompany));
        }
        return persistedCompany;
    }

    @Transactional
    public void deleteCompany(UUID id) {
        // delete any associated jobs first
        jobRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new CompanyDeletedEvent(this, id));
    }

    public List<Company> getAllSorted() {
        Pageable pageable = Pageable.ofSize(100);
        return companyRepository.findAllByOrderByName(pageable);
    }

    public Optional<Company> getById(UUID id) {
        return companyRepository.findById(id);
    }
}
