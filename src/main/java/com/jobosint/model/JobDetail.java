package com.jobosint.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record JobDetail(@Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Job job,
                        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Company company,
                        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) JobAttribute attributes,
                        @Column("applied_at") LocalDate appliedAt) {
    public String appliedAtDisplay() {
        if (this.appliedAt == null) {
            return "n/a";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd");
        return this.appliedAt.format(formatter);
    }
}
