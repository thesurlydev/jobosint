package com.jobosint.controller;

import com.jobosint.model.form.SearchForm;
import com.jobosint.service.CompanyService;
import com.jobosint.service.ContactService;
import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SearchFormController {

    private final ContactService contactService;
    private final CompanyService companyService;
    private final JobService jobService;

    @GetMapping("/search")
    public String search(Model model) {
        var searchForm = new SearchForm();
        model.addAttribute("searchForm", searchForm);
        return "search";
    }

    @PostMapping("/search")
    public String jobSubmit(Model model, @ModelAttribute SearchForm searchForm) {

        model.addAttribute("searchForm", searchForm);

        var contactResults = contactService.searchContacts(searchForm.getQuery());
        model.addAttribute("contactResults", contactResults);
        var numContactResults = contactResults.size();
        model.addAttribute("numContactResults", numContactResults);

        var companyResults = companyService.search(searchForm.getQuery());
        model.addAttribute("companyResults", companyResults);
        var numCompanyResults = companyResults.size();
        model.addAttribute("numCompanyResults", numCompanyResults);

        var jobResults = jobService.searchJobs(searchForm.getQuery());
        model.addAttribute("jobResults", jobResults);
        var numJobResults = jobResults.size();
        model.addAttribute("numJobResults", numJobResults);

        return "search";
    }
}
