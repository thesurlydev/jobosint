package com.jobosint.collaboration;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.exception.ToolInvocationException;
import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.task.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class Crew {

    private final ApplicationContext applicationContext;

    @Value("classpath:/prompts/crew-choose-agent.st")
    private Resource chooseAgentUserPrompt;

    public Map<String, AgentService> agents() {
        return applicationContext.getBeansOfType(AgentService.class);
    }

    public Map<String, AgentService> enabledAgents() {
        return agents().entrySet().stream()
                .filter(entry -> !entry.getValue().getDisabled())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public TaskResult processTask(ChatClient chatClient, Task task) throws ToolInvocationException {
        return chooseAgent(chatClient, task)
                .map(this::getAgent)
                .map(agent -> agent.processTask(task))
                .orElseThrow(() -> {
                    log.error("No agent available to process task: {}", task);
                    return new ToolInvocationException("No agent available to process task");
                });
    }

    public AgentService getAgent(String agentName) {
        return applicationContext.getBean(agentName, AgentService.class);
    }

    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public Optional<String> chooseAgent(ChatClient chatClient, Task task) {
        Map<String, AgentService> agents = enabledAgents();

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
