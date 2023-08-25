package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record Company(@Id UUID id, @Column("companyName") String name, @Column("companyUrl") String url) {


}
