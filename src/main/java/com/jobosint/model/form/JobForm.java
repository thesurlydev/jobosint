package com.jobosint.model.form;

import com.jobosint.model.Job;
import lombok.Getter;

import java.util.UUID;

@Getter
public class JobForm {

    private UUID id;
    private String title;
    private String url;
    private UUID companyId;

    private String source;

    private String salaryMin;
    private String salaryMax;

    private String notes;

    public JobForm() {
    }

    public JobForm(Job job) {
        this.id = job.id();
        this.notes = job.notes();
        this.companyId = job.companyId();
        this.salaryMin = job.salaryMin();
        this.salaryMax = job.salaryMax();
        this.source = job.source();
        this.title = job.title();
        this.url = job.url();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSalaryMin(String salaryMin) {
        this.salaryMin = salaryMin;
    }

    public void setSalaryMax(String salaryMax) {
        this.salaryMax = salaryMax;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
