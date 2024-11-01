package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record Page(@Id UUID id,
                   String url,
                   @Column("content_path") String contentPath,
                   String source) {
}
