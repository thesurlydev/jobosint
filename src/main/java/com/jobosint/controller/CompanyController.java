package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/companies")
    public Iterable<Company> getCompanies() {
        return companyRepository.findAll();
    }
}
