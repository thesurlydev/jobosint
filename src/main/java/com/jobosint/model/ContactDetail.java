package com.jobosint.model;

import org.springframework.data.relational.core.mapping.Embedded;

public record ContactDetail(@Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Contact contact,
                            @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Company company) {
}
