package com.jobosint.model.extension;

import com.jobosint.model.Page;

import java.nio.file.Path;

public record SavePageRequest(String url,
                              String content,
                              String source) {

    public Page toPage(Path contentPath) {
        return new Page(null, this.url, contentPath.toString(), this.source);
    }
}
