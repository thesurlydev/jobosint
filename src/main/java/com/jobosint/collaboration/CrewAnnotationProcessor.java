package com.jobosint.collaboration;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Tool;
import com.jobosint.collaboration.tool.ToolMetadata;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@Getter
@Component
public class CrewAnnotationProcessor {

    private final Set<AgentService> agents = new HashSet<>();

    public CrewAnnotationProcessor(ApplicationContext applicationContext) {
        Map<String, AgentService> agentBeans = applicationContext.getBeansOfType(AgentService.class);
        agentBeans.values().forEach(this::addTools);
        agents.addAll(agentBeans.values());
    }

    private void addTools(AgentService agent) {
        Arrays.stream(agent.getClass().getDeclaredMethods())
                .filter(this::hasToolAnnotation)
                .map(this::createTool)
                .forEach(agent::addTool);
    }

    private boolean hasToolAnnotation(Method method) {
        return AnnotationUtils.findAnnotation(method, Tool.class) != null;
    }

    private ToolMetadata createTool(Method method) {
        Tool tool = AnnotationUtils.findAnnotation(method, Tool.class);
        String name = Objects.requireNonNull(tool).name() != null ? tool.name() : "";
        String description = tool.description() != null ? tool.description() : "";
        boolean disabled = tool.disabled();
        return new ToolMetadata(name, description, method, disabled);
    }
}
