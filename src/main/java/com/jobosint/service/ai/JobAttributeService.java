package com.jobosint.service.ai;

import com.jobosint.config.AppConfig;
import com.jobosint.model.JobAttribute;
import com.jobosint.model.ai.JobAttributes;
import com.jobosint.repository.JobAttributeRepository;
import com.jobosint.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
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
    private final ChatModel chatModel;
    private final TokenizerService tokenizerService;
    private final JobAttributeRepository jobAttributeRepository;
    private final AppConfig appConfig;

    public Optional<JobAttributes> parseJobDescription(String jobDescriptionContent) {

        if (!appConfig.jobAttributesEnabled()) {
            log.info("Job Attributes disabled; skipping parseJobDescription");
            return Optional.empty();
        }

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
                If no qualifications are mentioned, return empty list.
                
                Technology stack should be separated into programming languages, frameworks, databases, cloud providers, and cloud services.
                If no technology stack is mentioned, return empty list.
                
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

            generation = chatModel.call(prompt).getResult();
        } catch (OpenAiApiClientErrorException e) {
            log.error("Error calling OpenAI API", e);
            return Optional.empty();
        }

        JobAttributes jobAttributes = outputParser.parse(generation.getOutput().getContent());
        return Optional.of(jobAttributes);
    }



    public void saveJobAttributes(JobAttribute jobAttribute) {
        jobAttributeRepository.save(jobAttribute);
    }
}
