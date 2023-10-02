package com.jobosint.model;

import java.nio.file.Path;

public record DownloadImageRequest(String url,
                                   Path targetDir,
                                   String targetFilename,
                                   boolean overwrite) {
}
