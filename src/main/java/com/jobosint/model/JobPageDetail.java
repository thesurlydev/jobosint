package com.jobosint.model;

import java.util.UUID;

public record JobPageDetail(UUID id, String pageId, String jobBoardId, String source, String pageUrl) {
}
