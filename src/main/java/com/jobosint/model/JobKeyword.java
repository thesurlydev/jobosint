package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record JobKeyword(@Id @Column("job_id") UUID jobId, String keyword) {
}
