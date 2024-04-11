package com.jobosint.integration.greenhouse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.integration.greenhouse")
public class GreenhouseConfig {
    private String baseUrl;
    private String downloadDir;
    private Boolean fetchJobsEnabled;
    private Boolean saveToDbEnabled;
    private Boolean saveToFileEnabled;
}
