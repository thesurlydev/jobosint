package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {
        log.info("Received: {}", event);
    }
}
