package com.jobosint.model.browse;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record BrowserPageUrl(@Id UUID id,
                             @Column("browser_page_id") UUID browserPageId,
                             String url,
                             String text) {
    public BrowserPageUrl(UUID browserPageId, String url, String text) {
        this(null, browserPageId, url, text);
    }
}
