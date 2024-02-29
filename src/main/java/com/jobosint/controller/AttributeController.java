package com.jobosint.controller;


import com.jobosint.model.Attribute;
import com.jobosint.model.AttributeValue;
import com.jobosint.model.form.AttributeForm;
import com.jobosint.service.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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

    @PostMapping("/attributes")
    public String attributes(Model model, @ModelAttribute AttributeForm attributeForm) {

        Attribute attr = new Attribute(null, attributeForm.getName(), Set.of());
        Attribute pAttr = attributeService.saveAttribute(attr);
        log.info("Saved attribute: {}", pAttr);
        Set<AttributeValue> values = Arrays.stream(attributeForm.getValuesCsv()
                        .split(","))
                .map(String::trim)
                .map(val -> new AttributeValue(null, pAttr.id(), val))
                .collect(Collectors.toSet());
        Iterable<AttributeValue> pAttrValues = attributeService.saveAttributeValues(values);
        log.info("Saved attribute values: {}", pAttrValues);
        return "redirect:/attributes";
    }
}
