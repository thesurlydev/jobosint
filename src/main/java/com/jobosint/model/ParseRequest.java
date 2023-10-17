package com.jobosint.model;

import java.util.List;

public record ParseRequest(java.nio.file.Path path,
                           String baseUrl,
                           String containerClass,
                           String parentContainerQuery,
                           String tag,
                           String tagClass,
                           List<String> containing,
                           List<String> excluding,
                           String attrValue,
                           boolean textOnly
) {
}
