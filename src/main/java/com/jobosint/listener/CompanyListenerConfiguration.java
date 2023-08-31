package com.jobosint.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CompanyListenerConfiguration {

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
