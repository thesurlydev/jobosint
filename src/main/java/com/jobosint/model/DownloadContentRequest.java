package com.jobosint.model;

import org.springframework.http.HttpMethod;

public record DownloadContentRequest(HttpMethod httpMethod,
                                     String url,
                                     String targetDir,
                                     boolean overwrite,
                                     String localFileSuffixToAppend) {
}
