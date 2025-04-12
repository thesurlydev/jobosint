package com.jobosint.model.browse;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record BrowserSession(@Id UUID id,
                             String name,
                             @Column("start_url") String startUrl,
                             @Column("created_at") @org.springframework.data.annotation.CreatedDate LocalDateTime createdAt) {

    public BrowserSession(String name, String startUrl) {
        this(null, name, startUrl, LocalDateTime.now());
    }
}
