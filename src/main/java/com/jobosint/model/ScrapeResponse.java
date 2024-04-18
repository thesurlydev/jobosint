package com.jobosint.model;

import java.util.EnumMap;
import java.util.Set;

public record ScrapeResponse(ScrapeRequest request,
                             Set<String> errors,
                             Set<String> data,
                             EnumMap<FetchAttribute, String> downloadPaths,
                             String base_url) {
}
