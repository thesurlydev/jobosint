package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.List;
import java.util.UUID;

public record JobAttribute(@Id UUID id,
                           @Column("job") UUID jobId,
                           @Column("interview_steps") List<String> interviewSteps,
                           @Column("programming_languages") List<String> programmingLanguages,
                           List<String> databases,
                           List<String> frameworks,
                           @Column("cloud_services") List<String> cloudServices,
                           @Column("cloud_providers") List<String> cloudProviders,
                           @Column("required_qualifications") List<String> requiredQualifications,
                           @Column("preferred_qualifications") List<String> preferredQualifications,
                           @Column("culture_values") List<String> cultureValues) {
}
