package com.jobosint.model.ext;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record Page(@Id UUID id,
                   String url,
                   @Column("raw_content") String content,
                   String source) {
}
