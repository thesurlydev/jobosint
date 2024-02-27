package com.jobosint.model.greenhouse;

import java.util.List;

public record GreenhouseJobsResponse(List<Job> jobs, Meta meta) {
}
