package com.jobosint.controller;

import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.Page;
import com.jobosint.model.extension.SavePageRequest;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.service.PageService;
import com.jobosint.util.UrlUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RequestMapping("/api/pages")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;
    private final LinkedInParser linkedInParser;

    @PostMapping("/parse")
    @Operation(summary = "Parse a page")
    public ResponseEntity<JobDescriptionParserResult> parsePage(@RequestBody SavePageRequest savePageRequest) {
        log.info("Parsing page from {}", savePageRequest.url());
        try {
            String decodedContent = UrlUtils.decodeContent(savePageRequest.content());
            JobDescriptionParserResult jobDescriptionParserResult = linkedInParser.parseJobDescriptionFromContent(decodedContent);
            return ResponseEntity.ok(jobDescriptionParserResult);

        } catch (Exception e) {
            log.error("Error parsing page: {}", savePageRequest, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping()
    @Operation(summary = "Save a page")
    public ResponseEntity<Page> savePage(@RequestBody SavePageRequest savePageRequest) {
        log.info("SavePageRequest: {} from {}", savePageRequest.url(), savePageRequest.source());
        Page savedPage;
        try {
            String decodedContent = UrlUtils.decodeContent(savePageRequest.content());
            Path savedPath = pageService.saveContent(decodedContent);
            Page page = savePageRequest.toPage(savedPath);
            savedPage = pageService.savePage(page, savePageRequest.cookies());
        } catch (Exception e) {
            log.error("Error saving page: {}", savePageRequest, e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(savedPage);
    }


}
