package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

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
        Boolean discontinued
) {

    public static String calcHash(String... parts) {
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            out.append(part);
        }
        return Base64.getEncoder().withoutPadding().encodeToString(out.toString().getBytes());
    }
}
