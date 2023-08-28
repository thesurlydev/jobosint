package com.jobosint.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyListenerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CompanyListenerConfiguration.class);

    @Bean
    CommandLineRunner startListener(CompanyNotifierService companyNotifierService, CompanyNotificationHandler companyNotificationHandler) {
        return (args) -> {
            Runnable listener =  companyNotifierService.createCompanyNotificationHandler(companyNotificationHandler);
            Thread t = new Thread(listener, "company-listener");
            t.start();
            log.info("Started company-listener");
        };
    }
}
