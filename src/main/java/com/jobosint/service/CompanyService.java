package com.jobosint.service;

import com.jobosint.listener.CompanyNotifierService;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyNotifierService companyNotifierService;

    public CompanyService(CompanyRepository companyRepository, CompanyNotifierService companyNotifierService) {
        this.companyRepository = companyRepository;
        this.companyNotifierService = companyNotifierService;
    }

    @Transactional
    public Company createCompany(Company company) {
        var persistedCompany = companyRepository.save(company);
        companyNotifierService.notifyCompanyCreated(persistedCompany);
        return company;
    }
}
