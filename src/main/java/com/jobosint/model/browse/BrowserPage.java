package com.jobosint.model.browse;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record BrowserPage(@Id UUID id,
                          @Column("browser_session_id") UUID browserSessionId,
                          @Column("url") String url,
                          @Column("content_path") String contentPath) {

    public BrowserPage(UUID browserSessionId, String url) {
        this(null, browserSessionId, url, "todo");
    }
}
