package com.jobosint.listener;

import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CompanyNotificationHandler implements Consumer<PGNotification> {

    private static final Logger log = LoggerFactory.getLogger(CompanyNotificationHandler.class);
    @Override
    public void accept(PGNotification pgNotification) {
        log.info("Received notification: name={}, id={}", pgNotification.getName(), pgNotification.getParameter());
    }
}
