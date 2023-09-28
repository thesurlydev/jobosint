package com.jobosint.model;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
public class JobDescriptionParseResult {
    private Integer errorCount;
    private Optional<List<String>> errorMessages;
    private Optional<JobDescription> jobDescription;
}
