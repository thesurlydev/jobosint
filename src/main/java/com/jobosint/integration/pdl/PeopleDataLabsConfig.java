package com.jobosint.integration.pdl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.integrations.pdl")
public class PeopleDataLabsConfig {
    private String baseUrl;
    private String apiKey;
}
