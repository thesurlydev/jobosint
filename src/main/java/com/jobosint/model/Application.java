package com.jobosint.model;

import com.jobosint.model.form.ApplicationForm;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record Application(@Id UUID id,
                          @Column("created_at") LocalDateTime createdAt,
                          @Column("updated_at") LocalDateTime updatedAt,
                          @Column("job_title") String jobTitle,
                          @Column("company") UUID companyId,
                          String url,
                          @Column("salary_min") String salaryMin,
                          @Column("salary_max") String salaryMax,
                          String status,
                          String source,
                          String notes) {

    public String salaryDisplay() {
        if (this.salaryMin == null && this.salaryMax == null) {
            return "n/a";
        } else if (this.salaryMin != null && this.salaryMax == null) {
            return this.salaryMin + "- ?";
        } else {
            return this.salaryMin + "-" + this.salaryMax;
        }
    }

    public static Application fromForm(ApplicationForm form) {
        return new Application(form.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                form.getTitle(),
                form.getCompanyId(),
                form.getUrl(),
                form.getSalaryMin(),
                form.getSalaryMax(),
                form.getStatus(),
                form.getSource(),
                form.getNotes()
        );
    }
}
