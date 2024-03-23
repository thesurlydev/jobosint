package com.jobosint.model;

import org.springframework.data.relational.core.mapping.Embedded;

public record ApplicationDetail(@Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Application app,
                                @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Company company) {
}
