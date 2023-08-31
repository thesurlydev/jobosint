package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyRestController {

    private final CompanyService companyService;

    @GetMapping()
    @Operation(summary = "Get all companies")
    public Iterable<Company> getCompanies() {
        return companyService.getAllSorted();
    }

    @PostMapping()
    @Operation(summary = "Save a company")
    public Company save(@RequestBody Company company) {
        return companyService.saveCompany(company);
    }
}
