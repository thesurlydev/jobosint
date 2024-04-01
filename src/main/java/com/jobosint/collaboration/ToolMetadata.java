package com.jobosint.collaboration;

import java.lang.reflect.Method;

public record ToolMetadata(String name, String description, Method method) {
}
