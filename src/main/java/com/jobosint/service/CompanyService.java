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

    public Company mergeCompany(Company company1, Company company2) {
        UUID id = company1.id();
        String name = company1.name() != null ? company1.name() : company2.name();
        String websiteUrl = company1.websiteUrl() != null ? company1.websiteUrl() : company2.websiteUrl();
        String stockTicker = company1.stockTicker() != null ? company1.stockTicker() : company2.stockTicker();
        String employeeCount = company1.employeeCount() != null ? company1.employeeCount() : company2.employeeCount();
        String summary = company1.summary() != null ? company1.summary() : company2.summary();
        String location = company1.location() != null ? company1.location() : company2.location();

        Company mergedCompany = new Company(id, name, websiteUrl, stockTicker, employeeCount, summary, location);

        return companyRepository.save(mergedCompany);
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
