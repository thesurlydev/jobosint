package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public record Page(@Id String id,
                   String url,
                   @Column("content_path") String contentPath,
                   String source) {
}
