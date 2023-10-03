package com.jobosint.model;

public record DownloadContentRequest(String url,
                                     String targetDir,
                                     boolean overwrite) {
}
