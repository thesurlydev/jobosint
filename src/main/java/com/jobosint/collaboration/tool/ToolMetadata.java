package com.jobosint.collaboration.tool;

import com.jobosint.collaboration.agent.AgentService;

import java.lang.reflect.Method;

public record ToolMetadata(String name, String description, Object agent, Method method, boolean disabled) {
    @Override
    public String toString() {
        return "ToolMetadata{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", agent=" + ((AgentService) agent).getName() +
                ", method=" + method +
                ", disabled=" + disabled +
                '}';
    }
}
