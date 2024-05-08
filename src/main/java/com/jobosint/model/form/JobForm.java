package com.jobosint.model.form;

import com.jobosint.model.Job;
import lombok.Data;

import java.util.UUID;

@Data
public class JobForm {

    private UUID id;
    private String title;
    private String url;
    private UUID companyId;
    private String jobBoardId;
    private UUID pageId;

    private String source;
    private String status;

    private String salaryMin;
    private String salaryMax;

    private String notes;

    private String content;

    public JobForm() {
    }

    public JobForm(Job job) {
        this.id = job.id();
        this.notes = job.notes();
        this.companyId = job.companyId();
        this.jobBoardId = job.jobBoardId();
        this.pageId = job.pageId();
        this.salaryMin = job.salaryMin();
        this.salaryMax = job.salaryMax();
        this.source = job.source();
        this.status = job.status();
        this.title = job.title();
        this.url = job.url();
        this.content = job.content();
    }
}
