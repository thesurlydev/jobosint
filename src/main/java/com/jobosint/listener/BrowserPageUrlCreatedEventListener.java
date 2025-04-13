package com.jobosint.listener;

import com.jobosint.event.BrowserPageUrlCreatedEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BrowserPageUrlCreatedEventListener implements ApplicationListener<BrowserPageUrlCreatedEvent> {
    @Override
    public void onApplicationEvent(@NonNull BrowserPageUrlCreatedEvent event) {
        log.info("Received: {}", event);
    }
}
