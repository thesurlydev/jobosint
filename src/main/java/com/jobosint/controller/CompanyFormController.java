package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.form.CompanyForm;
import com.jobosint.service.ApplicationService;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequiredArgsConstructor
public class CompanyFormController {
    private final CompanyService companyService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    @GetMapping("/companies/{id}")
    public String companyDetail(@PathVariable UUID id, Model model) {
        var company = companyService.getById(id);
        if (company.isPresent()) {
            model.addAttribute("company", company.get());

            model.addAttribute("companyJobs", jobService.getJobsByCompany(id));
            model.addAttribute("companyApps", applicationService.getApplicationsByCompany(id));

            return "companyDetail";
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Company not found");
        }
    }

    @GetMapping("/companies/{id}/delete")
    public RedirectView deleteCompany(@PathVariable UUID id, Model model) {
        companyService.deleteCompany(id);
        return new RedirectView("/companies");
    }

    @GetMapping("/company")
    public String companyForm(Model model) {
        model.addAttribute("company", new CompanyForm());
        return "/companyForm";
    }

    @GetMapping("/companies/{id}/edit")
    public String editCompany(@PathVariable UUID id, Model model) {
        var company = companyService.getById(id);
        company.ifPresent(c -> model.addAttribute("company", CompanyForm.fromCompany(c)));
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
        var company = new Company(companyForm.getId(), companyForm.getName(), companyForm.getWebsiteUrl(),
                companyForm.getStockTicker(), companyForm.getEmployeeCount(), companyForm.getSummary(),
                companyForm.getLocation(), companyForm.getLinkedinToken(), companyForm.getGreenhouseToken());
        companyService.saveCompany(company);
        return new RedirectView("/companies");
    }

}
