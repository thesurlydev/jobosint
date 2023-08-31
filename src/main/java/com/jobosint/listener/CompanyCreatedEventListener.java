package com.jobosint.listener;

import com.jobosint.event.CompanyCreatedEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CompanyCreatedEventListener implements ApplicationListener<CompanyCreatedEvent> {
    @Override
    public void onApplicationEvent(@NonNull CompanyCreatedEvent event) {
        log.info("Received: {}", event);
    }
}
