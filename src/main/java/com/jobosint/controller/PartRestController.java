package com.jobosint.controller;

import com.jobosint.service.PartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/parts")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PartRestController {
    private final PartService partService;

    @GetMapping("/refresh")
    public void callAi() {
//        partService..example("Portland, Oregon");
        // TODO
    }

}
