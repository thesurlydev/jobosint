package com.jobosint.config;

import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MardownReaderConfig {

    @Bean
    public MarkdownDocumentReaderConfig.Builder markdownDocumentReaderConfig() {
        return MarkdownDocumentReaderConfig.builder();
    }
}
