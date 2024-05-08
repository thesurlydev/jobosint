package com.jobosint.integration.linkedin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.integration.linkedin")
public class LinkedInConfig {
    private Boolean checkJobsStatusEnabled;
    private String checkJobsStatusCron;
}
