package com.jobosint.controller;

import com.jobosint.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping("/parts")
    public String parts(Model model) {
        var parts = partService.getAllParts();
        model.addAttribute("parts", parts);
        return "parts";
    }
}
