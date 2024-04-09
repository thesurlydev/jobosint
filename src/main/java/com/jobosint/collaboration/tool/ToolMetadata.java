package com.jobosint.collaboration.tool;

import java.lang.reflect.Method;
import java.util.Arrays;

public record ToolMetadata(String name, String description, Method method, boolean disabled) {
    public Class<?> getReturnType() {
        if (method.getParameterCount() == 0) {
            return null;
        }
        return Arrays.stream(method.getParameterTypes()).findFirst().orElseThrow();
    }

    public String getMethodArgsAsString() {
        // Get the parameter types
        Class<?>[] parameterTypes = method.getParameterTypes();

        // Build the string representation of the parameter types
        StringBuilder parametersString = new StringBuilder("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            parametersString.append(parameterTypes[i].getTypeName());
            if (i < parameterTypes.length - 1) {
                parametersString.append(", ");
            }
        }
        parametersString.append(")");

        return parametersString.toString();
    }
}
