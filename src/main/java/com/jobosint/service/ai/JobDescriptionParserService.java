package com.jobosint.service.ai;

import com.jobosint.model.ai.JobDescriptionParseResult;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class JobDescriptionParserService {

    private final ChatClient chatClient;
    private final JobDescriptionParser jobDescriptionParser;
    private final LinkedInParser linkedInParser;
    private final TokenizerService tokenizerService;

    public Optional<JobDescriptionParseResult> parseJobDescription(String path) {
        var outputParser = new BeanOutputParser<>(JobDescriptionParseResult.class);

        // first let's pare down the raw content
        File contentFile = new File(path);

        if (!contentFile.exists()) {
            log.error("File not found: {}", path);
            return Optional.empty();
        }

        String jd;
        try {
            jd = linkedInParser.parseJobDescription(path);
            Files.writeString(Path.of("jd.txt"), jd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // determine the number of tokens in the raw text
        Integer tokenCount = tokenizerService.countTokens(jd);
        log.info("token count: {}", tokenCount);

        // TODO if the token count exceeds the limit, we should return an error


        String userMessage =
                """
                        Given the following job description return details about the job {jd}.
                        Responsibilities and qualifications should be copied verbatim from the given job description.
                        {format}
                        """;

        PromptTemplate promptTemplate = new PromptTemplate(
                userMessage, Map.of("jd", jd, "format",
                outputParser.getFormat())
        );
        Prompt prompt = promptTemplate.create();
        Generation generation;
        try {
            generation = chatClient.call(prompt).getResult();
        } catch (OpenAiApi.OpenAiApiClientErrorException e) {
            log.error("Error calling OpenAI API", e);
            return Optional.empty();
        }

        return Optional.of(outputParser.parse(generation.getOutput().getContent()));
    }
}
