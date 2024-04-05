package com.jobosint.collaboration.agent;

import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.exception.ToolInvocationException;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.collaboration.tool.ToolMetadata;
import com.jobosint.collaboration.tool.ToolRegistry;
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

import java.util.*;

@RequiredArgsConstructor
@ToString
@Slf4j
public class AgentService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ToolRegistry toolRegistry;

    @Value("classpath:/prompts/agent-choose-tool.st")
    private Resource chooseAgentUserPrompt;

    @Getter
    private final String name;
    @Getter
    private final String goal;
    @Getter
    private final String background;
    @Getter
    private final Boolean disabled;
    @Getter
    private final String[] tools;

    public AgentService() {
        this.name = this.getClass().getSimpleName();
        if (this.getClass().isAnnotationPresent(Agent.class)) {
            Agent metadata = this.getClass().getAnnotation(Agent.class);
            this.goal = metadata.goal();
            this.background = metadata.background();
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
        if (tools.length == 0) {
            log.info("Agent has no tools configured, processing task via LLM");
            return processTaskViaLLM(task);
        }
        ToolMetadata toolMetadata = chooseTool(task);
        try {
            Object toolResult = invokeToolForTask(toolMetadata, task);
            return new TaskResult(toolResult);
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
        return new TaskResult(answer);
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

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentUserPrompt, Map.of(
                "task", task.getDescription(),
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
