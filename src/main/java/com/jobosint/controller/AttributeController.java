package com.jobosint.controller;


import com.jobosint.model.Attribute;
import com.jobosint.model.form.AttributeForm;
import com.jobosint.service.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping("/attributes")
    public String attributes(Model model) {
        var attributes = attributeService.getAllAttributes();
        model.addAttribute("attributes", attributes);
        model.addAttribute("attributeForm", new AttributeForm());
        return "attributes";
    }

    @DeleteMapping("/attributes/{id}")
    public String deleteAttribute(@PathVariable UUID id) {
        attributeService.deleteAttribute(id);
        return "redirect:/attributes";
    }


    @PostMapping("/attributes")
    public String attributes(Model model, @ModelAttribute AttributeForm attributeForm) {
        Attribute attr = new Attribute(null, attributeForm.getName(), attributeForm.getValues());
        Attribute pAttr = attributeService.saveAttribute(attr);
        log.info("Saved attribute: {}", pAttr);
        return "redirect:/attributes";
    }
}
