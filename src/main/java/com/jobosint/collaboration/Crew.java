package com.jobosint.collaboration;

import com.jobosint.collaboration.agent.AgentRegistry;
import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.exception.ToolInvocationException;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskAssignment;
import com.jobosint.collaboration.task.TaskResult;
import com.jobosint.collaboration.tool.ToolMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class Crew {

    private final AgentRegistry agentRegistry;

    @Value("classpath:/prompts/crew-choose-agent.st")
    private Resource chooseAgentUserPrompt;

    @Value("classpath:/prompts/crew-choose-agents.st")
    private Resource chooseAgentsUserPrompt;

    public List<TaskAssignment> assignTasks(ChatClient chatClient, List<Task> tasks) {
        return tasks.stream()
                .map(task -> new TaskAssignment(task, chooseAgent(chatClient, task).orElse(null)))
                .toList();
    }

    public Map<String, Object> chooseAgents(ChatClient chatClient, List<Task> tasks) {
        Map<String, AgentService> agents = agentRegistry.enabledAgents();

        if (agents.isEmpty()) {
            log.warn("No agents available");
            return Map.of();
        }

        log.info("Found {} enabled agents", agents.size());
        agents.forEach((k, v) -> log.info("Agent: {}, Goal: {}", k, v.getGoal()));

        var outputParser = new MapOutputParser();

        StringBuilder agentList = new StringBuilder();
        for (Map.Entry<String, AgentService> entry : agents.entrySet()) {
            var agentName = entry.getKey();
            var agent = entry.getValue();
            var agentGoal = agent.getGoal();
            var tools = agent.getTools();
            StringBuilder toolList = generateToolListForPrompt(tools);
            agentList.append(agentName).append(": ").append(agentGoal)
                    .append("\r\n")
                    .append(toolList);
        }

        StringBuilder taskList = new StringBuilder();
        for (Task task : tasks) {
            taskList.append(task.getDescription()).append("\r\n");
        }

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentsUserPrompt, Map.of(
                "tasks", taskList.toString(),
                "agents", agentList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();

        return outputParser.parse(out);
    }

    private @NotNull StringBuilder generateToolListForPrompt(Map<String, ToolMetadata> tools) {
        StringBuilder toolList = new StringBuilder();
        toolList.append("The tools available from this agent are: ");
        for (Map.Entry<String, ToolMetadata> toolEntry : tools.entrySet()) {
            var toolName = toolEntry.getKey();
            var toolDesc = toolEntry.getValue().description();
            boolean toolDisabled = toolEntry.getValue().disabled();
            if (!toolDisabled) {
                toolList.append("- ").append(toolName).append(": ").append(toolDesc).append("\r\n");
            }
        }
        return toolList;
    }

    public TaskResult processTask(ChatClient chatClient, Task task) throws ToolInvocationException {
        log.info("Processing task: {}", task);
        return chooseAgent(chatClient, task)
                .map(agentRegistry::getAgent)
                .map(agent -> agent.processTask(task))
                .orElseThrow(() -> {
                    log.error("No agent available to process task: {}", task);
                    return new ToolInvocationException("No agent available to process task");
                });
    }


    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public Optional<String> chooseAgent(ChatClient chatClient, Task task) {
        Map<String, AgentService> agents = agentRegistry.enabledAgents();

        log.info("Found {} enabled agents", agents.size());

        if (task.getAgent() != null) {
            log.info("Task specified agent: {}", task.getAgent());
            return agents.keySet().stream().filter(s -> s.equals(task.getAgent())).findFirst();
        }

        agents.forEach((k, v) -> log.info("Agent: {}, Goal: {}", k, v.getGoal()));

        if (agents.isEmpty()) {
            log.warn("No agents available");
            return Optional.empty();
        }

        if (agents.size() == 1) {
            var agent = agents.entrySet().stream().findFirst().get();
            log.warn("Only one agent available: {}", agent);
            return Optional.ofNullable(agent.getKey());
        }

        var outputParser = new BeanOutputParser<>(String.class);

        StringBuilder agentList = new StringBuilder();
        for (Map.Entry<String, AgentService> entry : agents.entrySet()) {
            agentList.append(entry.getKey()).append(": ").append(entry.getValue().getGoal()).append("\r\n");
        }

        PromptTemplate promptTemplate = new PromptTemplate(chooseAgentUserPrompt, Map.of(
                "task", task.getDescription(),
                "agents", agentList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();
        String out = generation.getOutput().getContent();
        log.info("Selected Agent: {}", out);

        return Optional.ofNullable(outputParser.parse(out));
    }

}
