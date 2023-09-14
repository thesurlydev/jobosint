package com.jobosint.controller;

import com.jobosint.model.form.SearchForm;
import com.jobosint.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SearchFormController {

    private final CompanyService companyService;

    @GetMapping("/search")
    public String search(Model model) {
        var searchForm = new SearchForm();
        model.addAttribute("searchForm", searchForm);
        return "search";
    }

    @PostMapping("/search")
    public String jobSubmit(Model model, @ModelAttribute SearchForm searchForm) {
        var results = companyService.search(searchForm.getQuery());
        model.addAttribute("numResults", results.size());
        model.addAttribute("results", results);
        return "search";
    }
}
