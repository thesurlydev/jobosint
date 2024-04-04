package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.agent.Agent;
import com.jobosint.collaboration.annotation.AgentMeta;
import com.jobosint.collaboration.annotation.Tool;
import com.jobosint.collaboration.task.DeconstructedTask;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.Map;

@AgentMeta(
        goal= "Given a task, break it down into smaller sub-tasks",
        tools= {"TaskDeconstructor"}
)
@RequiredArgsConstructor
public class TaskDeconstructor extends Agent {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/task-deconstruct-task.st")
    private Resource deconstructTaskUserPrompt;

    @Tool(name = "DeconstructTask", description = "Break down a task into smaller sub-tasks")
    public DeconstructedTask deconstructTask(Task task) {
        var outputParser = new BeanOutputParser<>(DeconstructedTask.class);
        PromptTemplate promptTemplate = new PromptTemplate(deconstructTaskUserPrompt, Map.of(
                "task", task.getDescription(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return outputParser.parse(generation.getOutput().getContent());
    }
}
