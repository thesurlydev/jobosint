package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "prices")
public record Price(
        @Id UUID id,
        UUID vendorId,
        String partNumber,
        BigDecimal price,
        Boolean available,
        String sku,
        String vendorUrl,
        PartCondition condition
) {
}
