package com.jobosint.controller;

import com.jobosint.model.Company;
import com.jobosint.model.form.ContactForm;
import com.jobosint.service.CompanyService;
import com.jobosint.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ContactController {
    private final CompanyService companyService;
    private final ContactService contactService;

    @GetMapping("/contact")
    public String contactForm(Model model) {
        List<Company> companies = companyService.getAllSorted();
        model.addAttribute("companyLookup", companies);
        var contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "/contactForm";
    }

    @PostMapping("/contact")
    public RedirectView contactSubmit(@ModelAttribute ContactForm contactForm) {
        var contact = contactForm.toContact();
        contactService.saveContact(contact);
        return new RedirectView("contacts");
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        var contacts = contactService.getAllContactDetailOrderByName();
        model.addAttribute("contacts", contacts);
        return "contacts";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editJob(@PathVariable UUID id, Model model) {
        var maybeContact = contactService.getContact(id);
        maybeContact.ifPresent(contact -> {
            var contactForm = new ContactForm(contact);
            model.addAttribute("contactForm", contactForm);
            List<Company> companies = companyService.getAllSorted();
            model.addAttribute("companyLookup", companies);
        });
        return "contactForm";
    }
}
