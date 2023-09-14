package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "serp")
public record SerpApiConfig(String apiKey) {
}
