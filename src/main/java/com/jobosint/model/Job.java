package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record Job(@Id UUID id, @Column("company") UUID companyId, String title, String url, @Column("created_at") LocalDateTime createdAt, String status) {

}
