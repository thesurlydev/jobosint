package com.jobosint.model.form;

import com.jobosint.model.Application;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplicationForm {
    private UUID id;
    private String title;
    private UUID companyId;
    private String salaryMax;
    private String salaryMin;
    private String source;
    private String status;
    private String notes;
    private String url;

    public ApplicationForm() {
    }

    public ApplicationForm(Application app) {
        this.id = app.id();
        this.title = app.jobTitle();
        this.companyId = app.companyId();
        this.salaryMax = app.salaryMax();
        this.salaryMin = app.salaryMin();
        this.source = app.source();
        this.status = app.status();
        this.notes = app.notes();
        this.url = app.url();
    }
}
