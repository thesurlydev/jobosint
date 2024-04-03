package com.jobosint.collaboration.agent;

import com.jobosint.collaboration.Task;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.exception.ToolInvocationException;
import com.jobosint.collaboration.exception.ToolNotFoundException;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.collaboration.tool.ToolMetadata;
import com.jobosint.collaboration.tool.ToolRegistry;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final Boolean disabled;
    @Getter
    private final String[] tools;

    public Agent() {
        this.name = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(AgentMeta.class)) {
            AgentMeta metadata = this.getClass().getAnnotation(AgentMeta.class);
            this.goal = metadata.goal();
            this.disabled = metadata.disabled();
            this.tools = metadata.tools();
        } else {
            throw new IllegalStateException("Agent annotation is required on Agent classes");
        }
    }

    private Object invokeToolForTask(ToolMetadata toolMetadata, Task task) throws Exception {
        return toolRegistry.invokeTool(toolMetadata, task);
    }

    public TaskResult processTask(Task task) throws ToolInvocationException {
        ToolMetadata toolMetadata = chooseTool(task);
        try {
            Object toolResult = invokeToolForTask(toolMetadata, task);
            return new TaskResult(toolResult);
        } catch (Exception e) {
            throw new ToolInvocationException("Error invoking tool: " + toolMetadata + " for task: " + task, e);
        }
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

        List<String> agentToolNames = Arrays.stream(tools).toList();

        StringBuilder toolList = new StringBuilder();
        toolRegistry.getTools()
                .stream()
                .filter(toolMetadata -> agentToolNames.contains(toolMetadata.name()))
                .map(toolMetadata -> toolMetadata.name() + ": " + toolMetadata.description() + "\r\n")
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

        String out = generation.getOutput().getContent();
        log.info("Generation output:\n{}\n", out);

        String toolName = outputParser.parse(out);
        log.info("Selected tool name: {}", toolName);

        ToolMetadata selectedTool = toolRegistry.getTool(toolName);
        log.info("Selected tool: {}", selectedTool);

        return selectedTool;
    }


}
