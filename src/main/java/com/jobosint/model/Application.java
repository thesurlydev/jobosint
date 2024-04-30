package com.jobosint.model;

import com.jobosint.model.form.ApplicationForm;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record Application(@Id UUID id,
                          @Column("created_at") LocalDateTime createdAt,
                          @Column("updated_at") LocalDateTime updatedAt,
                          @Column("applied_at") LocalDate appliedAt,
                          @Column("job") UUID jobId,
                          String status,
                          String notes,
                          String resume) {


    public static Application fromJob(JobDetail jobDetail) {
        return new Application(null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDate.now(),
                jobDetail.job().id(),
                "Applied",
                null,
                null
        );
    }

    public static Application fromForm(ApplicationForm form) {
        return new Application(form.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                form.getAppliedAt(),
                form.getJobId(),
                form.getStatus(),
                form.getNotes(),
                form.getResume()
        );
    }
}
