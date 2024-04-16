package com.jobosint.integration.greenhouse.model;

import java.time.OffsetDateTime;
import java.util.List;

public record Job(String absolute_url,
                  Long id,
                  String title,
                  String content,
                  OffsetDateTime updated_at,
                  List<Department> departments,
                  List<Office> offices
) {
    @Override public String toString() {
        return String.format("%s: %s", this.id, this.title);
    }

    public Job updateContent(String newContent) {
        return new Job(absolute_url, id, title, newContent, updated_at, departments, offices);
    }
}
