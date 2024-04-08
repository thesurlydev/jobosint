package com.jobosint.collaboration.agent;

import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.exception.ToolInvocationException;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.collaboration.tool.ToolMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.lang.reflect.Method;
import java.util.*;

@RequiredArgsConstructor
@ToString
@Slf4j
public class AgentService {

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/prompts/agent-choose-tool.st")
    private Resource chooseAgentUserPrompt;

    @Value("classpath:/prompts/tool-registry-choose-tool-args.st")
    private Resource chooseToolArgsUserPrompt;

    @Getter
    private final String name;
    @Getter
    private final String goal;
    @Getter
    private final String background;
    @Getter
    private final Boolean disabled;
    @Getter
    private final Map<String, ToolMetadata> tools = new HashMap<>();

    public AgentService() {
        this.name = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(Agent.class)) {
            Agent annotation = this.getClass().getAnnotation(Agent.class);
            this.goal = annotation.goal();
            this.background = annotation.background();
            this.disabled = annotation.disabled();
        } else {
            throw new IllegalStateException("Agent annotation is required on Agent classes");
        }
    }

    public void addTool(ToolMetadata toolMetadata) {
        tools.put(toolMetadata.name(), toolMetadata);
    }

    private Object invokeToolForTask(ToolMetadata toolMetadata, Task task) throws Exception {
        log.info("Invoking tool: {} for task: {}", toolMetadata, task);
        return invokeTool(toolMetadata, task);
    }

    public TaskResult processTask(Task task) throws ToolInvocationException {
        if (tools.isEmpty()) {
            log.info("Agent has no tools configured, processing task via LLM");
            return processTaskViaLLM(task);
        }
        ToolMetadata toolMetadata = chooseTool(task);
        try {
            Object toolResult = invokeToolForTask(toolMetadata, task);
            log.info("RESULT: \n\n{}\n", toolResult);
            return new TaskResult(this, toolResult);
        } catch (Exception e) {
            throw new ToolInvocationException("Error invoking tool: " + toolMetadata + " for task: " + task, e);
        }
    }

    private TaskResult processTaskViaLLM(Task task) {

        List<Message> messages = new ArrayList<>();

        if (this.background != null && !this.background.isEmpty()) {
            SystemPromptTemplate systemTemplate = new SystemPromptTemplate(this.background);
            messages.add(systemTemplate.createMessage());
        }

        var taskDescription = task.getDescription();
        UserMessage userMessage = new UserMessage(taskDescription);
        messages.add(userMessage);

        Prompt prompt = new Prompt(messages);
        log.info("PROMPT:\n\n{}\n", prompt);
        Generation generation = chatClient.call(prompt).getResult();
        String answer = generation.getOutput().getContent();
        return new TaskResult(this, answer);
    }

    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public ToolMetadata chooseTool(Task task) {
        log.info("Choosing tool for task: {}", task);

        StringBuilder toolList = new StringBuilder();
        tools.values().stream()
                .map(toolMetadata -> toolMetadata.name() + ": " + toolMetadata.description() + "\r\n")
                .forEach(toolList::append);

        var outputParser = new BeanOutputParser<>(String.class);

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentUserPrompt, Map.of(
                "task", task.getDescription(),
                "tools", toolList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();

        String out = generation.getOutput().getContent();

        String toolName = outputParser.parse(out);
        log.info("Selected tool name: {}", toolName);

        ToolMetadata selectedTool = tools.get(toolName);
        log.info("Selected tool: {}", selectedTool);

        return selectedTool;
    }

    public Object getToolArgs(ToolMetadata toolMetadata, Task task) {
        log.info("Getting '{}' args for task: {}", toolMetadata.name(), task.toString());

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
                "task", task.getDescription(),
                "signature", signature,
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        log.info("'{}' tool args:\n{}\n", toolMetadata.name(), out);

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

    public <T> T invokeTool(ToolMetadata toolMetadata, Task task) throws Exception {
        Method method = toolMetadata.method();
        log.info("Method: {}", method.getName());
        String toolName = toolMetadata.name();
        log.info("Tool name: {}", toolName);
        Object args = getToolArgs(toolMetadata, task);
        log.info("Args: {}", args);

        if (args == null) {
            T result = (T) method.invoke(this);
            return result;
        } else {
            T result = (T) method.invoke(this, args);
            return result;
        }
    }


}
