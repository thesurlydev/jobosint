package com.jobosint.service;

import com.jobosint.event.CompanyCreatedEvent;
import com.jobosint.event.CompanyDeletedEvent;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    @Transactional
    public Company saveCompany(Company company) {
        boolean isNew = company.id() == null;
        var persistedCompany = companyRepository.save(company);
        if (isNew) {
            applicationEventPublisher.publishEvent(new CompanyCreatedEvent(this, persistedCompany));
        }
        return company;
    }

    @Transactional
    public void deleteCompany(UUID id) {
        // delete any associated jobs first
        jobRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new CompanyDeletedEvent(this, id));
    }

    public List<Company> getAllSorted() {
        return companyRepository.findByOrderByNameAsc();
    }

    public Optional<Company> getById(UUID id) {
        return companyRepository.findById(id);
    }
}
