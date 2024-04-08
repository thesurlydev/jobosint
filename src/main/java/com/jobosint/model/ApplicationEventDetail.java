package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

public record ApplicationEventDetail(@Id UUID id,
                                     @Column("applicationId") UUID applicationId,
                                     @Column("interview_type") String interviewType,
                                     @Column("event_type") String eventType,
                                     @Column("event_date") LocalDateTime eventDate,
                                     Set<String> tools,
                                     String notes,

                                     @Column("jobId") UUID jobId,
                                     @Column("jobTitle") String jobTitle,

                                     @Column("companyId") UUID companyId,
                                     @Column("companyName") String companyName,
                                     @Column("interviewer_id") UUID interviewerId,

                                     @Column("interviewerFullName") String interviewerFullName,
                                     @Column("interviewerTitle") String interviewerTitle
) {
    public String interviewerNameTitleDisplay() {
        StringBuilder out = new StringBuilder();
        if (this.interviewerFullName != null) {
            out.append(this.interviewerFullName);
        }
        if (this.interviewerTitle != null) {
            out.append("<br/>").append(this.interviewerTitle);
        }
        return out.toString();
    }

    public String jobTitleCompanyDisplay() {
        return this.jobTitle + " @" + this.companyName;
    }

    public String eventDateDisplay() {
        if (this.eventDate == null) {
            return "n/a";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd hh:mm a");
        return this.eventDate.format(formatter);
    }
}
