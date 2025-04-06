package com.jobosint.model;

import java.time.LocalDateTime;

public record Resume(String id, String name, String content, LocalDateTime createdAt) {
}
