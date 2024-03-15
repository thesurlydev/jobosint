package com.jobosint.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@Deprecated
public class JobDescriptionServiceOpenAI {

    public static class JobDescriptionParserRequest {
        @JsonPropertyDescription("The content of the job description to parse")
        @JsonProperty(required = true)
        private String content;
    }

    @Data
    public static class JobDescriptionParserResponse {
        @JsonPropertyDescription("The company name")
        @JsonProperty(required = true)
        private String company;

        @JsonPropertyDescription("The company website URL")
        @JsonProperty(required = true)
        private String websiteUrl;

        @JsonPropertyDescription("The job requirements")
        @JsonProperty(required = true)
        private List<String> requirements;
    }

    public void parse(String content) {
        String token = System.getenv("OPENAI_API_KEY");
        OpenAiService service = new OpenAiService(token);

        FunctionExecutor functionExecutor = new FunctionExecutor(
                Collections.singletonList(
                        ChatFunction.builder()
                                .name("extractData")
                                .description("Given the result of parsing a job description return a structured response")
                                .executor(JobDescriptionParserResponse.class, s -> s)
                                .build()
                )
        );

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that parses a job description and returns a structured response.");
        messages.add(systemMessage);

        String contentMessage = "The job description content is the following: " + content;
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), contentMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(messages)
                .functions(functionExecutor.getFunctions())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .n(1)
//                .maxTokens(4000)
                .logitBias(new HashMap<>())
                .build();

        ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
        System.out.println(result.toString());
        ChatMessage responseMessage = result.getChoices().getFirst().getMessage();
        messages.add(responseMessage);


        for (ChatMessage chatMessage : messages) {
            log.info(chatMessage.toString());
        }
    }
}
