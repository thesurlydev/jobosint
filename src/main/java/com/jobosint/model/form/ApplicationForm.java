package com.jobosint.model.form;

import com.jobosint.model.ApplicationDetail;
import lombok.Data;

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
    }
}
