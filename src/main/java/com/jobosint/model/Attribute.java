package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record Attribute(@Id UUID id,
                        String name,
                        Set<AttributeValue> attributeValues) {

    public List<String> getAttributeValuesAsStrings() {
        return attributeValues.stream()
                .map(AttributeValue::value)
                .collect(Collectors.toList());
    }
}
