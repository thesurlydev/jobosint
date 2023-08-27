package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public record Job(@Id UUID id,
                  @Column("company") UUID companyId,
                  String title,
                  String url,
                  @Column("created_at") LocalDateTime createdAt,
                  String status,
                  @Column("salary_min") Integer salaryMin,
                  @Column("salary_max") Integer salaryMax,
                  String source
) {

    public String salaryDisplay() {
        if (this.salaryMin == null && this.salaryMax == null) {
            return "n/a";
        } else if (this.salaryMin != null && this.salaryMax == null) {
            return String.valueOf(this.salaryMin);
        } else {
            return new StringJoiner("-")
                    .add(String.valueOf(this.salaryMin))
                    .add(String.valueOf(this.salaryMax))
                    .toString();
        }
    }
}
