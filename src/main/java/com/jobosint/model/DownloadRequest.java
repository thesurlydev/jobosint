package com.jobosint.model;

import java.net.http.HttpRequest;
import java.nio.file.Path;

public record DownloadRequest(HttpRequest httpRequest,
                              Path targetDir,
                              String targetFilename,
                              boolean overwrite) {
    public Path localPath() {
        return targetDir.resolve(targetFilename);
    }
}