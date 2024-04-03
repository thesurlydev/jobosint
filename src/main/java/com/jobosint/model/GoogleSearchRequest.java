package com.jobosint.model;

public record GoogleSearchRequest(String query, Integer maxResults) {
    public GoogleSearchRequest(String query, Integer maxResults) {
        this.query = query;
        this.maxResults = maxResults == null ? 10 : maxResults;
    }
}
