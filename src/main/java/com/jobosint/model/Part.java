package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

/**
 * TODO: supersessions, weight, images, required number, part name code (PNC)
 * @param id
 * @param partNumber
 * @param name
 * @param description
 * @param manufacturerId
 * @param category
 * @param subcategory
 * @param msrpPrice
 */
@Table(name = "parts")
public record Part(
        @Id UUID id,
        String partNumber, // vendor specific
        String name,
        String description,
        UUID manufacturerId,
        String category,
        String subcategory,
        BigDecimal msrpPrice,
        PartClassification classification
) {
}
