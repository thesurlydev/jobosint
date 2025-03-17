package com.jobosint.service.ai;

import com.jobosint.model.ai.CompanyDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyDetailsService {

    private final ChatModel chatModel;

    public CompanyDetail getCompanyDetails(String name) {
        var outputParser = new BeanOutputConverter<>(CompanyDetail.class);

        String userMessage =
                """
                Get the details including website url and address for the company: {name}.
                Only provide the stock ticker if the company is public.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("name", name, "format",
                outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();

        log.info("Prompt: {}", prompt.toString());

        Generation generation = chatModel.call(prompt).getResult();

        CompanyDetail detail = outputParser.convert(generation.getOutput().getText());
        log.info("CompanyDetail: {}", detail);
        return detail;
    }
}