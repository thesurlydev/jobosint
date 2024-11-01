package com.jobosint.listener;

import com.jobosint.event.CompanyDeletedEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CompanyDeletedEventListener implements ApplicationListener<CompanyDeletedEvent> {

    @Override
    public void onApplicationEvent(@NonNull CompanyDeletedEvent event) {
        log.info("Received: {}", event);
    }
}
