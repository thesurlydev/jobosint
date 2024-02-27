package com.jobosint.model.greenhouse;

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
}
