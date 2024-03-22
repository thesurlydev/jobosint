package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record Application(@Id UUID id,
                          @Column("job") UUID jobId,
                          @Column("created_at") LocalDateTime createdAt,
                          @Column("updated_at") LocalDateTime updatedAt,
                          @Column("applied_at") LocalDateTime appliedAt,
                          @Column("replied_at") LocalDateTime repliedAt,
                          String status,
                          String notes) {
}
