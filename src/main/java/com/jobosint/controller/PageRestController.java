package com.jobosint.controller;

import com.jobosint.model.ext.Page;
import com.jobosint.ai.WeatherServiceOpenAI;
import com.jobosint.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequestMapping("/api/pages")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;
    private final WeatherServiceOpenAI weatherServiceOpenAI;

    @PostMapping()
    @Operation(summary = "Save a page")
    public void savePage(@RequestBody Page page) {
        String data = page.content();

        try {
            StringBuilder tempBuffer = new StringBuilder();
            int incrementor = 0;
            int dataLength = data.length();
            while (incrementor < dataLength) {
                char charecterAt = data.charAt(incrementor);
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>");
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>");
                } else {
                    tempBuffer.append(charecterAt);
                }
                incrementor++;
            }
            data = tempBuffer.toString();

            data = URLDecoder.decode(data, StandardCharsets.UTF_8);
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch(Exception e) {
            e.printStackTrace();
        }

        Page p = new Page(null, page.url(), data, page.source());

        log.info("Saving page: {}", p.url());
        pageService.savePage(p);
    }

    @GetMapping("/ai")
    public void callAi() {
        weatherServiceOpenAI.example("Portland, Oregon");
    }
}
