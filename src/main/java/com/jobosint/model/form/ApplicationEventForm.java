package com.jobosint.model.form;

import com.jobosint.model.ApplicationEvent;
import com.jobosint.model.ApplicationEventDetail;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ApplicationEventForm {
    private UUID id;
    private UUID applicationId;
    private String eventType;
    private String interviewType;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm") // required for thymeleaf + datetime-local form field
    private LocalDateTime eventDate;
    private Set<String> tools;
    private String notes;
    private UUID interviewerId;

    public ApplicationEventForm() {
    }

    public ApplicationEventForm(ApplicationEventDetail detail) {
        this.id = detail.id();
        this.applicationId = detail.applicationId();
        this.eventType = detail.eventType();
        this.eventDate = detail.eventDate();
        this.tools = detail.tools();
        this.notes = detail.notes();
        this.interviewType = detail.interviewType();
        this.interviewerId = detail.interviewerId();
    }

    public ApplicationEvent toApplicationEvent() {
        return new ApplicationEvent(id, applicationId, interviewType, eventType, eventDate, tools, notes, interviewerId);
    }
}
