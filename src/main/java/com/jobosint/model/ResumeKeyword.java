package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record ResumeKeyword(@Id @Column("resume_id") UUID resumeId, String keyword) {
}
