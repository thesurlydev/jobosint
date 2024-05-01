package com.jobosint.model;

public record JobDescriptionParserResult(
        String title,
        String companyName,
        String companySlug,
        String description,
        SalaryRange salaryRange) {
}
