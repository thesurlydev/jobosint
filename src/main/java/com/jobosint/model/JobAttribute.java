package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

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
    public JobAttribute {
        interviewSteps = interviewSteps != null ? interviewSteps : emptyList();
        programmingLanguages = programmingLanguages != null ? programmingLanguages : emptyList();
        databases = databases != null ? databases : emptyList();
        frameworks = frameworks != null ? frameworks : emptyList();
        cloudServices = cloudServices != null ? cloudServices : emptyList();
        cloudProviders = cloudProviders != null ? cloudProviders : emptyList();
        requiredQualifications = requiredQualifications != null ? requiredQualifications : emptyList();
        preferredQualifications = preferredQualifications != null ? preferredQualifications : emptyList();
        cultureValues = cultureValues != null ? cultureValues : emptyList();
    }
}
