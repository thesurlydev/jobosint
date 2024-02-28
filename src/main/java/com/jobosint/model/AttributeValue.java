package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record AttributeValue(@Id UUID id,
                             @Column("attribute") UUID attributeId,
                             String value) {
}
