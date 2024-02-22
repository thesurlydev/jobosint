package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.form.CompanyForm;
import com.jobosint.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CompanyFormController {
    private final CompanyService companyService;

    @GetMapping("/company")
    public String companyForm(Model model) {
        model.addAttribute("company", new CompanyForm());
        return "/companyForm";
    }

    @GetMapping("/companies/{id}/delete")
    public RedirectView deleteCompany(@PathVariable UUID id, Model model) {
        companyService.deleteCompany(id);
        return new RedirectView("/companies");
    }

    @GetMapping("/companies/{id}/edit")
    public String editCompany(@PathVariable UUID id, Model model) {
        var company = companyService.getById(id);
        company.ifPresent(c -> model.addAttribute("company", new CompanyForm(c.id(), c.name(), c.websiteUrl())));
        return "/companyForm";
    }

    @GetMapping("/companies")
    public String companies(Model model) {
        var companies = companyService.getAllSorted();
        model.addAttribute("companies", companies);
        return "/companies";
    }

    @PostMapping("/company")
    public RedirectView companySubmit(@ModelAttribute CompanyForm companyForm) {
        var company = new Company(companyForm.getId(), companyForm.getName(), companyForm.getWebsiteUrl(), null, null
                ,null, null);
        companyService.saveCompany(company);
        return new RedirectView("/companies");
    }

}
