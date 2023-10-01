package com.jobosint.model;

public record DownloadImageRequest(String url,
                                   String dir,
                                   String filename) {
}
