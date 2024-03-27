package com.jobosint.model.form;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ApplicationEventForm {
    private UUID id;
    private UUID applicationId;
    private String eventType;
    private String interviewType;
    private LocalDateTime eventDate;
    private Set<String> tools;
    private String notes;
}
