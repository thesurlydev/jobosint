package com.jobosint.service.ai;

import com.jobosint.model.ai.TechnologyStack;
import com.jobosint.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class JobTechnologyStackService {
    private final ChatModel chatModel;
    private final TokenizerService tokenizerService;

    public Optional<TechnologyStack> parseJobDescription(String jobDescriptionContent) {
        var outputParser = new BeanOutputConverter<>(TechnologyStack.class);

        // determine the number of tokens in the raw text
        Integer tokenCount = tokenizerService.countTokens(jobDescriptionContent);
        log.info("token count: {}", tokenCount);

        // TODO if the token count exceeds the limit, we should return an error

        String userMessage = """
                Given the following job description extract the technology stack mentioned about the job:
                {jd}
                Technology stack should be separated into programming languages, frameworks, databases, cloud providers, and cloud services.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(
                userMessage, Map.of("jd", jobDescriptionContent, "format",
                outputParser.getFormat())
        );
        Prompt prompt = promptTemplate.create();
        Generation generation;
        try {
            generation = chatModel.call(prompt).getResult();
        } catch (OpenAiApiClientErrorException e) {
            log.error("Error calling OpenAI API", e);
            return Optional.empty();
        }

        TechnologyStack technologyStack = outputParser.convert(generation.getOutput().getContent());
        return Optional.ofNullable(technologyStack);
    }
}
