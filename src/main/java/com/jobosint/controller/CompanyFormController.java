package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.form.CompanyForm;
import com.jobosint.repository.CompanyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CompanyFormController {

    private final CompanyRepository companyRepository;

    public CompanyFormController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/company")
    public String companyForm(Model model) {
        model.addAttribute("company", new CompanyForm());
        return "/companyForm";
    }

    @PostMapping("/company")
    public String companySubmit(@ModelAttribute CompanyForm companyForm, Model model) {

        var company = new Company(null, companyForm.getName(), companyForm.getWebsiteUrl());
        Company savedCompany = companyRepository.save(company);

        companyForm.setId(savedCompany.id());

        model.addAttribute("company", companyForm);
        return "/companyDetail";
    }

}
