package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record Note(@Id UUID id, @Column("job") UUID jobId, String description, @Column("created_at") LocalDateTime createdAt) {


}

