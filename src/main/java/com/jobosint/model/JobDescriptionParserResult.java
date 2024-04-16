package com.jobosint.model;

public record JobDescriptionParserResult(
        String title,
        String companyName,
        String description,
        SalaryRange salaryRange) {
}
