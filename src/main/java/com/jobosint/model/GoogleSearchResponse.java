package com.jobosint.model;

import java.util.List;

public record GoogleSearchResponse(List<GoogleSearchResult> results) {
}
