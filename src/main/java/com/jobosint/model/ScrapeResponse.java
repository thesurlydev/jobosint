package com.jobosint.model;

import java.util.Set;

public record ScrapeResponse(ScrapeRequest request,
                             Set<String> errors,
                             Set<String> data,
                             String base_url) {
}
