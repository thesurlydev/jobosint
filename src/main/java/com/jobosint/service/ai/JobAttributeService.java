package com.jobosint.service.ai;

import com.jobosint.model.ai.InterviewProcess;
import com.jobosint.model.ai.JobAttributes;
import com.jobosint.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAttributeService {
    private final ChatClient chatClient;
    private final TokenizerService tokenizerService;

    public Optional<JobAttributes> parseJobDescription(String jobDescriptionContent) {
        var outputParser = new BeanOutputParser<>(JobAttributes.class);

        // determine the number of tokens in the raw text
        Integer tokenCount = tokenizerService.countTokens(jobDescriptionContent);
        log.info("token count: {}", tokenCount);

        // TODO if the token count exceeds the limit, we should return an error

        String userMessage =
                """
                Given the following job description extract:
                 - The interview process steps
                 - The qualifications for the job
                 - The technology stack
                 - The cultural values
                for the job:
                {jd}
                                
                Interview steps should be separated into ordered steps.
                If no interview process is mentioned, return empty list.
                
                Qualifications should be separated into required and preferred.
                
                Technology stack should be separated into programming languages, frameworks, databases, cloud providers, and cloud services.
                
                If no cultural values are mentioned, return empty list.
                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(
                userMessage, Map.of("jd", jobDescriptionContent, "format",
                outputParser.getFormat())
        );
        Prompt prompt = promptTemplate.create();
        Generation generation;
        try {
            generation = chatClient.call(prompt).getResult();
        } catch (OpenAiApiClientErrorException e) {
            log.error("Error calling OpenAI API", e);
            return Optional.empty();
        }

        JobAttributes jobAttributes = outputParser.parse(generation.getOutput().getContent());
        return Optional.of(jobAttributes);
    }
}