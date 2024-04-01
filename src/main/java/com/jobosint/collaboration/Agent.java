package com.jobosint.collaboration;

import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;
import com.jobosint.collaboration.exception.ToolInvocationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

@RequiredArgsConstructor
@ToString
@Slf4j
public class Agent {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolRegistry toolRegistry;

    @Getter
    private final String name;
    @Getter
    private final String goal;
    @Getter
    private final String[] tools;

    public Agent() {
        this.name = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(AgentMeta.class)) {
            AgentMeta metadata = this.getClass().getAnnotation(AgentMeta.class);
            this.goal = metadata.goal();
            this.tools = metadata.tools();
        } else {
            throw new IllegalStateException("Agent annotation is required on Agent classes");
        }
    }

    public <T> T invokeTool(String toolName) throws Exception {
        return toolRegistry.invokeTool(toolName);
    }

    public <T> Optional<T> processTask(Task task, Class<T> expectedType) throws ToolInvocationException {
        return chooseTool(task).map(tool -> {
            try {
                return expectedType.cast(invokeTool(tool.name()));
            } catch (Exception e) {
                log.error("Error invoking tool", e);
                throw new ToolInvocationException("Error invoking tool: " + tool.name(), e);
            }
        });
    }

    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public Optional<ToolMetadata> chooseTool(Task task) {

        StringBuilder toolList = new StringBuilder();
        toolRegistry.getTools().stream()
                .filter(tm -> Arrays.stream(this.tools).anyMatch(s -> s.equals(tm.name())))
                .map(tm -> tm.name() + ": " + tm.description() + "\r\n")
                .forEach(toolList::append);

        var outputParser = new BeanOutputParser<>(String.class);

        String userMessage = """
                Given a task and a list of tools, determine which of the tools is most capable of accomplishing the task.
                Return just the name of the tool.
                                        
                The task is:
                {task}
                                        
                Each tool has a name and a description. Here are the list of tools:
                {tools}
                                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of(
                "task", task.description(),
                "tools", toolList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();

        Optional<String> maybeToolName = Optional.ofNullable(outputParser.parse(generation.getOutput().getContent()));

        return maybeToolName.map(s -> toolRegistry.getTool(s));
    }
}
