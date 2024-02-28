package com.jobosint.controller;


import com.jobosint.service.AttributeService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping("/attributes")
    public String attributes(Model model) {
        var attributes = attributeService.getAllAttributes();
        model.addAttribute("attributes", attributes);
        return "attributes";
    }
}
