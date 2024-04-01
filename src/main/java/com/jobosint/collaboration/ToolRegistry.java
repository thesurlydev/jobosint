package com.jobosint.collaboration;

import com.jobosint.collaboration.annotation.Tool;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ToolRegistry implements BeanPostProcessor {

    // Map to store tool name and its corresponding method for quick access
    private final Map<String, Method> toolMethods = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();

    @Getter
    private final Set<ToolMetadata> tools = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(method, Tool.class) != null) {
                registerTools(bean);
            }
        }
        return bean;
    }

    public ToolMetadata getTool(String toolName) {
        return tools.stream().filter(t -> { return t.name().equals(toolName); }).findFirst().orElseThrow();
    }

    private void registerTools(Object service) {
        Class<?> serviceClass = service.getClass();
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Tool.class)) {
                Tool tool = method.getAnnotation(Tool.class);
                toolMethods.put(tool.name(), method);
                serviceInstances.put(tool.name(), service);
                tools.add(new ToolMetadata(tool.name(), tool.description(), method));
            }
        }
    }

    public <T> T invokeTool(String toolName) throws Exception {
        Method method = toolMethods.get(toolName);
        if (method != null) {
            Object serviceInstance = serviceInstances.get(toolName);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(serviceInstance);
            return result;
        } else {
            throw new IllegalArgumentException("Tool not found: " + toolName);
        }
    }
}
