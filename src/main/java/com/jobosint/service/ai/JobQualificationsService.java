package com.jobosint.service.ai;

import com.jobosint.model.ai.JobQualifications;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class JobQualificationsService {

    private final ChatClient chatClient;
    private final TokenizerService tokenizerService;

    public Optional<JobQualifications> parseJobDescription(String jobDescriptionContent) {
        var outputParser = new BeanOutputParser<>(JobQualifications.class);

        // determine the number of tokens in the raw text
        Integer tokenCount = tokenizerService.countTokens(jobDescriptionContent);
        log.info("token count: {}", tokenCount);

        // TODO if the token count exceeds the limit, we should return an error

        String userMessage =
                """
                Given the following job description extract qualifications about the job:
                {jd}
                Qualifications should be separated into required and preferred.
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

        JobQualifications jobQualifications = outputParser.parse(generation.getOutput().getContent());
        return Optional.of(jobQualifications);
    }
}
