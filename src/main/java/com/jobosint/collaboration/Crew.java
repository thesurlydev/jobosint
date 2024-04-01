package com.jobosint.collaboration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class Crew {

    @Autowired private ChatClient chatClient;
    @Autowired private ApplicationContext applicationContext;

    public Map<String, Agent> agents() {
        return applicationContext.getBeansOfType(Agent.class);
    }

    /**
     * Given a task, determine which agent is most capable of accomplishing the task
     * TODO add tools to prompt to aid in decisioning
     *
     * @param task
     * @return
     */
    public Optional<String> chooseAgent(Task task) {
        Map<String, Agent> agents = agents();
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
        for (Map.Entry<String, Agent> entry: agents.entrySet()) {
            agentList.append(entry.getKey()).append(": ").append(entry.getValue().getGoal()).append("\r\n");
        }

        String userMessage =
        """
                Given a task and a list of agents, determine which of the agents is most capable of accomplishing the task.
                Return just the name of the agent.
                
                The task is:
                {task}
                
                Each agent has a name and a goal. Here are the list of agents:
                {agents}
                                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of(
                "task", task.description(),
                "agents", agentList.toString(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();

        return Optional.ofNullable(outputParser.parse(generation.getOutput().getContent()));
    }

}
