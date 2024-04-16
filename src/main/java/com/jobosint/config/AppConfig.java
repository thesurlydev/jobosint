package com.jobosint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app")
public record AppConfig(Path pageDir, Boolean jobAttributesEnabled) {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    /*@Bean
    @Primary // ensures that this overrides the default one provided by Spring Boot
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule());
        return mapper;
    }*/
}
