package com.jobosint.model;

import org.springframework.data.relational.core.mapping.Embedded;

public record JobDetail(@Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Job job,
                        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Company company) {

}
