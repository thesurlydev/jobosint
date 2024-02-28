package com.jobosint.model;

import java.util.Set;

public record AttributeDetail(Attribute attribute,
                              Set<AttributeValue> attributeValues) {
}
