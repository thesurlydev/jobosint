package com.jobosint.model;

import com.jobosint.model.form.JobForm;
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
                  String source,

                  @Column("contact_name") String contactName,
                  @Column("contact_email") String contactEmail,
                  @Column("contact_phone") String contactPhone
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

    public static Job fromForm(JobForm form) {
        return new Job(form.getId(),
                form.getCompanyId(),
                form.getTitle(),
                form.getUrl(),
                null,
                null,
                form.getSalaryMin(),
                form.getSalaryMax(),
                form.getSource(),
                form.getContactName(),
                form.getContactEmail(),
                form.getContactPhone()
        );
    }
}
