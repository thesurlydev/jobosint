package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.util.Objects;
import java.util.UUID;

public final class Job {
    @Id
    private final UUID id;
    private final String title;
    private final String url;
    AggregateReference<Company, UUID> company;

    public Job(UUID id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public AggregateReference<Company, UUID> getCompany() {
        return company;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Job) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.company, that.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, url, company);
    }

    @Override
    public String toString() {
        return "Job[" +
                "id=" + id + ", " +
                "title=" + title + ", " +
                "url=" + url + ", " +
                "company=" + company + ']';
    }

}
