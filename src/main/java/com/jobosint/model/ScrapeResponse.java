package com.jobosint.model;

import java.util.*;

public final class ScrapeResponse {
    private final ScrapeRequest request;
    private Set<String> errors;
    private final Set<String> data;
    private final EnumMap<FetchAttribute, String> downloadPaths;
    private final String base_url;

    public ScrapeResponse(ScrapeRequest request,
                          Set<String> errors,
                          Set<String> data,
                          EnumMap<FetchAttribute, String> downloadPaths,
                          String base_url) {
        this.request = request;
        this.errors = errors;
        this.data = data;
        this.downloadPaths = downloadPaths;
        this.base_url = base_url;
    }

    public ScrapeResponse(ScrapeRequest request,
                          String error) {
        this.request = request;
        this.errors = Collections.singleton(error);
        this.data = null;
        downloadPaths = null;
        base_url = null;
    }

    @Override
    public String toString() {
        return "ScrapeResponse(request=" + this.request() + ", errors=" + this.errors() + ", data=..." + ", downloadPaths=" + this.downloadPaths() + ", base_url=" + this.base_url() + ")";
    }

    public ScrapeRequest request() {
        return request;
    }

    public Set<String> errors() {
        return errors;
    }

    public Set<String> data() {
        return data;
    }

    public EnumMap<FetchAttribute, String> downloadPaths() {
        return downloadPaths;
    }

    public String base_url() {
        return base_url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScrapeResponse) obj;
        return Objects.equals(this.request, that.request) &&
                Objects.equals(this.errors, that.errors) &&
                Objects.equals(this.data, that.data) &&
                Objects.equals(this.downloadPaths, that.downloadPaths) &&
                Objects.equals(this.base_url, that.base_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, errors, data, downloadPaths, base_url);
    }

}
