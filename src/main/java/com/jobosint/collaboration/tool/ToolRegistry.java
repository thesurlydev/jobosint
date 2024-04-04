package com.jobosint.collaboration.tool;

import com.jobosint.collaboration.Task;
import com.jobosint.collaboration.annotation.Tool;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class ToolRegistry implements BeanPostProcessor {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/tool-registry-choose-tool-args.st")
    private Resource chooseToolArgsUserPrompt;

    // Map to store tool name and its corresponding method for quick access
    private final Map<String, Method> toolMethods = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();

    @Getter
    private final Set<ToolMetadata> tools = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NotNull String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(method, Tool.class) != null) {
                registerTools(bean);
            }
        }
        return bean;
    }

    public ToolMetadata getTool(String toolName) {
        return tools.stream().filter(t -> t.name().equals(toolName)).findFirst().orElseThrow();
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

    public <T> T invokeTool(ToolMetadata toolMetadata, Task task) throws Exception {
        Method method = toolMetadata.method();
        log.info("Method: {}", method.getName());
        String toolName = toolMetadata.name();
        log.info("Tool name: {}", toolName);
        Object args = getToolArgs(toolMetadata, task);
        log.info("Args: {}", args);

        Object serviceInstance = serviceInstances.get(toolName);
        log.info("Service instance: {}", serviceInstance.getClass().getName());

        if (args == null) {
            T result = (T) method.invoke(serviceInstance);
            return result;
        } else {
            T result = (T) method.invoke(serviceInstance, args);
            return result;
        }
    }

    public Object getToolArgs(ToolMetadata toolMetadata, Task task) {

        if (toolMetadata.method().getParameterCount() == 0) {
            return null;
        }

        Class<?> paramType = Arrays.stream(toolMetadata.method().getParameterTypes()).findFirst().orElseThrow();
        log.info("Method parameter type: {}", paramType.getName());

        String signature = getMethodArgsAsString(toolMetadata.method());
        log.info("Method signature: {}", signature);

        var outputParser = new BeanOutputParser<>(paramType);

        String format = outputParser.getFormat();
        log.info("Output parser format:\n{}\n", format);

        PromptTemplate promptTemplate = new PromptTemplate(chooseToolArgsUserPrompt, Map.of(
                "task", task.description(),
                "signature", signature,
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        log.info("Generation output:\n{}\n", out);

        return outputParser.parse(out);
    }

    public static String getMethodArgsAsString(Method method) {
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
