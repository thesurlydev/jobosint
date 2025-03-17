package com.jobosint.model.extension;

import com.jobosint.model.Page;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SavePageRequest(String url,
                              String content,
                              String source,
                              List<Map<String, String>> cookies) {

    public Page toPage(Path contentPath) {
        return new Page(null, this.url, contentPath.toString(), this.source);
    }
}
