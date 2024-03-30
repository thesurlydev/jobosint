package com.jobosint.model;

import lombok.Data;

import java.util.UUID;

@Data
public class ScrapeHttpError {

    private UUID id;
    private final String error;
    public ScrapeHttpError(String error) {
        this.id = UUID.randomUUID();
        this.error = error;
    }
}
