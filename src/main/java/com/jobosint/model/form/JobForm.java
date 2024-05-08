package com.jobosint.model.form;

import com.jobosint.model.Job;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class JobForm {

    private UUID id;
    private String title;
    private String url;
    private UUID companyId;
    private String jobBoardId;
    private UUID pageId;
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm a") // required for thymeleaf + Tempus Dominus date picker form field
    private LocalDateTime createdAt;

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
        this.createdAt = job.createdAt();
    }
}
