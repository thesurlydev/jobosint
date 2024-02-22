package com.jobosint.service.ai;

import com.jobosint.model.JobDescription;
import com.jobosint.model.ai.JobDescriptionParseResult;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.parse.ParseResult;
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

    public Optional<JobDescriptionParseResult> parseJobDescription() {
        var outputParser = new BeanOutputParser<>(JobDescriptionParseResult.class);

        String filePath = "/home/shane/projects/jobosint/data/pages/20240219-0800/https-www-linkedin" +
                "-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid" +
                "-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html";

        // first let's pare down the raw content

        String jd;
        try {
            jd = linkedInParser.parseJobDescription(filePath);
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
