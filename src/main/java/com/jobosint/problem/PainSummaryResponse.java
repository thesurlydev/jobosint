package com.jobosint.problem;

public record PainSummaryResponse(String summary,
                                  java.util.List<Complaint> commonComplaints,
                                  java.util.List<Recommendation> recommendations) {
}
