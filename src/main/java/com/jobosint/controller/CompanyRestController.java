package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyRestController {

    private final CompanyRepository companyRepository;

    public CompanyRestController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping()
    public Iterable<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @PostMapping()
    public Company insertCompany(@RequestBody Company company) {
        Company savedCompany = companyRepository.save(company);
        return savedCompany;
    }
}
