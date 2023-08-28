package com.jobosint.service;

import com.jobosint.event.CompanyDeletedEvent;
import com.jobosint.listener.CompanyNotifierService;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyRepository companyRepository;
    private final CompanyNotifierService companyNotifierService;

    public CompanyService(ApplicationEventPublisher applicationEventPublisher,
                          CompanyRepository companyRepository,
                          CompanyNotifierService companyNotifierService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.companyRepository = companyRepository;
        this.companyNotifierService = companyNotifierService;
    }

    @Transactional
    public Company createCompany(Company company) {
        var persistedCompany = companyRepository.save(company);
        companyNotifierService.notifyCompanyCreated(persistedCompany);
        return company;
    }

    @Transactional
    public void deleteCompany(UUID id) {
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
