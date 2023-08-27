package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record Job(@Id UUID id, @Column("company") UUID companyId, String title, String url) {

}
