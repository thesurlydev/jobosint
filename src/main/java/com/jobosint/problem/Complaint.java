package com.jobosint.problem;

public record Complaint(String description, Integer relativeSeverityScoreOneToTen, java.util.List<Source> sources) {
}
