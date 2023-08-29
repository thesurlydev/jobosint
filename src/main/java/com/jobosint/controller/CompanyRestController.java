package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.service.CompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyRestController {

    private final CompanyService companyService;

    public CompanyRestController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping()
    public Iterable<Company> getCompanies() {
        return companyService.getAllSorted();
    }

    @PostMapping()
    public Company save(@RequestBody Company company) {
        return companyService.saveCompany(company);
    }
}
