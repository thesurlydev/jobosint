package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "scrape")
public record ScrapeConfig(Path downloadPath,
                           String namespace,
                           String harFilename,
                           String screenshotFilename,
                           String htmlFilename) {
}