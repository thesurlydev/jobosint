package com.jobosint.controller;

import com.jobosint.service.ApplicationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventController {
    private final ApplicationEventService applicationEventService;
}
