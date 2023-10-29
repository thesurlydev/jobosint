package com.jobosint.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

@RequiredArgsConstructor
@Data
public class YoshiPriceRequest {
    private final List<String> productUids;
    private static final String URL = "https://api.yoshiparts.com/flat-offers";

    public String toJson(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public HttpRequest httpRequest() {
        String body = this.toJson();
        return HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
