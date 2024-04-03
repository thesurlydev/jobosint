package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.Task;
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

import java.util.Map;

@AgentMeta(
        goal= "Given a task, break it down into smaller sub-tasks",
        tools= {"TaskDeconstructor"}
)
@RequiredArgsConstructor
public class TaskPlanner extends Agent {

    private final ChatClient chatClient;

    @Tool(name = "TaskDeconstructor", description = "Break down a task into smaller sub-tasks")
    public DeconstructedTask deconstructTask(Task task) {
        var outputParser = new BeanOutputParser<>(DeconstructedTask.class);

        String userMessage = """
                Given a sentence with potentially multiple actions, break it down into sub-tasks.
                Each sub-task should be a single action.
                Usually a task should be broken down into subtasks if it is too complex to be accomplished in one go.
                If the task does not need to be broken down into subtasks, set the sub-tasks to an empty list.
                
                The task is:
                {task}
                                                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of(
                "task", task.description(),
                "format", outputParser.getFormat()
        ));
        Prompt prompt = promptTemplate.create();

        Generation generation = chatClient.call(prompt).getResult();

        return outputParser.parse(generation.getOutput().getContent());
    }
}
