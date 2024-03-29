package com.jobosint.integration.pdl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PeopleDataLabsClient {

    private final PeopleDataLabsConfig config;
    private final RestTemplate restTemplate;

    public Optional<CompanyEnrichResponse> enrichCompany(String companyName) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.set("accept", MediaType.APPLICATION_JSON.toString());
        headers.set("X-API-Key", config.getApiKey());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getBaseUrl())
                .queryParam("name", companyName);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CompanyEnrichResponse> response = restTemplate
                .exchange(builder.toUriString(), HttpMethod.GET, entity, CompanyEnrichResponse.class);

        return response.getStatusCode().is2xxSuccessful() ? Optional.ofNullable(response.getBody()) : Optional.empty();
    }
}
