package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crunchbase")
public record CrunchbaseConfig(String apiKey, String baseUrl) {
}
