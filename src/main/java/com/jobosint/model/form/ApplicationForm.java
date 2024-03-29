package com.jobosint.model.form;

import com.jobosint.model.ApplicationDetail;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApplicationForm {
    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String jobUrl;
    private String companyName;
    private String status;
    private String notes;
    @DateTimeFormat(pattern = "yyyy-MM-dd") // required for date form field
    private LocalDate appliedAt;

    public ApplicationForm() {
    }

    public ApplicationForm(ApplicationDetail detail) {
        this.id = detail.id();
        this.jobId = detail.jobId();
        this.jobTitle = detail.jobTitle();
        this.jobUrl = detail.jobUrl();
        this.companyName = detail.companyName();
        this.status = detail.status();
        this.notes = detail.notes();
        this.appliedAt = detail.appliedAt();
    }
}
