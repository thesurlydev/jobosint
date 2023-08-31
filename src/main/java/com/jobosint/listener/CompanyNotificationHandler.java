package com.jobosint.listener;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class CompanyNotificationHandler implements Consumer<PGNotification> {

    @Override
    public void accept(PGNotification pgNotification) {
        log.info("Received notification: name={}, id={}", pgNotification.getName(), pgNotification.getParameter());
    }
}
