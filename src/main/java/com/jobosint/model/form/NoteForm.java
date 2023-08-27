package com.jobosint.model.form;

import java.util.UUID;

public class NoteForm {
    private UUID id;
    private UUID jobId;
    private String description;

    public NoteForm() {
    }

    public NoteForm(UUID jobId) {
        this.jobId = jobId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
