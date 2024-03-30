package com.jobosint.controller;

import com.jobosint.exception.InvalidScrapeRequestException;
import com.jobosint.model.*;
import com.jobosint.service.ScrapeService;
import com.microsoft.playwright.PlaywrightException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

@RequestMapping("/api/scrape")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ScrapeController {


    private final ScrapeService scrapeService;

    @GetMapping()
    @ResponseBody
    public ScrapeResponse scrape(@RequestParam String url,
                                 @RequestParam String sel,
                                 @RequestParam(required = false, defaultValue = "text") SelectAttribute attr,
                                 @RequestParam(required = false) String attrVal,
                                 @RequestParam(required = false) Set<FetchAttribute> fetch) {

        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        var req = new ScrapeRequest(url, sel, attr, attrVal, fetch);
        req.validate();

        return scrapeService.scrape(req);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            InvalidScrapeRequestException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ScrapeHttpError handleValidationExceptions(Throwable ex) {
        return new ScrapeHttpError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PlaywrightException.class)
    public ScrapeHttpError handlePlaywrightExceptions(PlaywrightException ex) {
        log.error("PlaywrightException: {}", ex.getMessage());
        if (ex.getMessage().contains("net::ERR_CERT_AUTHORITY_INVALID")) {
            return new ScrapeHttpError("SSL certificate error");
        } else if (ex.getMessage().contains("net::ERR_NAME_NOT_RESOLVED")) {
            return new ScrapeHttpError("Could not resolve host");
        } else {
            return new ScrapeHttpError("Internal server error");
        }
    }
}
