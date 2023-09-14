package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pdl")
public record PeopleDataLabsConfig(String apiKey) {
}
