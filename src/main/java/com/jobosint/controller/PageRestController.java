package com.jobosint.controller;

import com.jobosint.model.ext.Page;
import com.jobosint.openai.OpenAIService;
import com.jobosint.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/pages")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;
    private final OpenAIService openAIService;

    @PostMapping()
    @Operation(summary = "Save a page")
    public void savePage(@RequestBody Page page) {
        log.info("Page: {}", page);
        pageService.savePage(page);
    }

    @GetMapping("/ai")
    public void callAi() {
        openAIService.example("Portland, Oregon");
    }
}
