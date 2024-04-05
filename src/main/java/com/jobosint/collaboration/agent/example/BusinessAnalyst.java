package com.jobosint.collaboration.agent.example;

import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;
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

@Agent(
        goal = "Provide business analysis of companies",
        tools = {"GetCompanyDetail", "GetCompanyFocus"}
)
@RequiredArgsConstructor
public class BusinessAnalyst extends AgentService {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/agent-company-focus.st")
    private Resource companyFocusUserPrompt;

    @Tool(name = "GetCompanyFocus", description = "Given the name of a company, return the focus of the company")
    public String getCompanyFocus(String companyName) {
        PromptTemplate promptTemplate = new PromptTemplate(companyFocusUserPrompt, Map.of(
                "companyName", companyName
        ));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return generation.getOutput().getContent();
    }
}
