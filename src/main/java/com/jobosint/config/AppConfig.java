package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app")
public record AppConfig(Path pageDir) {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
