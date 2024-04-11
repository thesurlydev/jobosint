package com.jobosint.integration.greenhouse.model;

import java.util.List;

public record GreenhouseJobsResponse(List<Job> jobs, Meta meta) {
}
