package com.jobosint.model.form;

import com.jobosint.model.Job;

import java.util.List;
import java.util.UUID;

public class JobForm {

    private UUID id;
    private String title;
    private String url;
    private UUID companyId;

    private String source;
    private String status;

    private Integer salaryMin;
    private Integer salaryMax;

    private String notes;

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JobForm() {
    }

    public JobForm(Job job) {
        this.id = job.id();
        this.notes = job.notes();
        this.companyId = job.companyId();
        this.contactEmail = job.contactEmail();
        this.contactName = job.contactName();
        this.contactPhone = job.contactPhone();
        this.salaryMin = job.salaryMin();
        this.salaryMax = job.salaryMax();
        this.source = job.source();
        this.status = job.status();
        this.title = job.title();
        this.url = job.url();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
