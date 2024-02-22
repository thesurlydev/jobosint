package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.service.CompanyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        // TODO

        // TODO: create new company record if it doesn't exist

        // TODO: create new job record



    }
}
