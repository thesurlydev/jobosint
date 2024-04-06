package com.jobosint.collaboration.tool;

import java.lang.reflect.Method;

public record ToolMetadata(String name, String description, Method method, boolean disabled) {

}
