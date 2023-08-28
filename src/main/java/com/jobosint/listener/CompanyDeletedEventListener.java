package com.jobosint.listener;

import com.jobosint.event.CompanyDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CompanyDeletedEventListener implements ApplicationListener<CompanyDeletedEvent> {

    private static final Logger log = LoggerFactory.getLogger(CompanyDeletedEventListener.class);

    @Override
    public void onApplicationEvent(CompanyDeletedEvent event) {
        log.info("Received: {}", event);
    }
}
