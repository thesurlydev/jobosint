package com.jobosint.controller;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.Page;
import com.jobosint.model.extension.SavePageRequest;
import com.jobosint.ai.WeatherServiceOpenAI;
import com.jobosint.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@RequestMapping("/api/pages")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;
    private final WeatherServiceOpenAI weatherServiceOpenAI;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping()
    @Operation(summary = "Save a page")
    public ResponseEntity<Page> savePage(@RequestBody SavePageRequest savePageRequest) {
        Page savedPage;
        try {
            String decodedContent = decodeContent(savePageRequest.content());
            Path savedPath = pageService.saveContent(savePageRequest.url(), decodedContent);
            Page page = savePageRequest.toPage(savedPath);
            savedPage = pageService.savePage(page);
            applicationEventPublisher.publishEvent(new PageCreatedEvent(this, savedPage));
        } catch (Exception e) {
            log.error("Error saving page: " + savePageRequest, e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(savedPage);
    }

    private String decodeContent(String data) {
        if (data == null) return null; // Early return for null input

        StringBuilder tempBuffer = new StringBuilder();
        try {
            for (char c : data.toCharArray()) {
                switch (c) {
                    case '%':
                        tempBuffer.append("<percentage>"); // Placeholder for '%'
                        break;
                    case '+':
                        tempBuffer.append("<plus>"); // Placeholder for '+'
                        break;
                    default:
                        tempBuffer.append(c);
                }
            }
            String decodedData = URLDecoder.decode(tempBuffer.toString(), StandardCharsets.UTF_8);
            return decodedData
                    .replaceAll("<percentage>", "%")
                    .replaceAll("<plus>", "+");
        } catch (Exception e) {
            log.error("Error fixing content", e);
            return null; // Return null or some error indication as per your error handling policy
        }
    }

    @GetMapping("/ai")
    public void callAi() {
        weatherServiceOpenAI.example("Portland, Oregon");
    }
}
