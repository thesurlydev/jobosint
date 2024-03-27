package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ApplicationEvent(@Id UUID id,
                               @Column("application") UUID applicationId,
                               @Column("interview_type") String interviewType,
                               @Column("event_type") String eventType,
                               @Column("event_date") LocalDateTime eventDate,
                               Set<String> tools,
                               String notes
) {
}
