package com.jobosint.controller;

import com.jobosint.model.ext.Page;
import com.jobosint.service.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/pages")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;

    @PostMapping()
    public void savePage(@RequestBody Page page) {
        log.info("Page: {}", page);
        pageService.savePage(page);
    }
}
