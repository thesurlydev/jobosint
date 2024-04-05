package com.jobosint.model;

public record GoogleSearchRequest(String query, Integer maxResults) {
    public GoogleSearchRequest(String query, Integer maxResults) {
        this.query = query;
        this.maxResults = (maxResults == null || maxResults == 0) ? 10 : maxResults;
    }
}
