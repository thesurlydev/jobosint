package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.form.CompanyForm;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
public class CompanyFormController {
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    public CompanyFormController(CompanyRepository companyRepository, CompanyService companyService) {
        this.companyRepository = companyRepository;
        this.companyService = companyService;
    }

    @GetMapping("/company")
    public String companyForm(Model model) {
        model.addAttribute("company", new CompanyForm());
        return "/companyForm";
    }

    @GetMapping("/companies/{id}/delete")
    public RedirectView deleteCompany(@PathVariable UUID id, Model model) {
        companyRepository.deleteById(id);
        return new RedirectView("/companies");
    }

    @GetMapping("/companies/{id}/edit")
    public String editCompany(@PathVariable UUID id, Model model) {
        var company = companyRepository.findById(id);
        company.ifPresent(c -> model.addAttribute("company", new CompanyForm(c.id(), c.name(), c.websiteUrl())));
        return "/companyForm";
    }


    @GetMapping("/companies")
    public String companies(Model model) {
        var companies = companyRepository.findAll();
        model.addAttribute("companies", companies);
        return "/companies";
    }

    @PostMapping("/company")
    public RedirectView companySubmit(@ModelAttribute CompanyForm companyForm) {
        var company = new Company(companyForm.getId(), companyForm.getName(), companyForm.getWebsiteUrl());
        companyService.createCompany(company);
        return new RedirectView("/companies");
    }

}
