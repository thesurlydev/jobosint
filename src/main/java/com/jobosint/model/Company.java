package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;
import java.util.UUID;

public final class Company {
    @Id
    private final UUID id;
    private final String name;
    private final String url;


    @PersistenceCreator
    public Company(
            UUID id,
            String name,
            String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Company) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url);
    }

    @Override
    public String toString() {
        return "Company[" +
                "uuid=" + id + ", " +
                "name=" + name + ", " +
                "url=" + url + ']';
    }

}
