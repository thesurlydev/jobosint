package com.jobosint.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OpenAIService {
    public static class Weather {
        @JsonPropertyDescription("City and state, for example: León, Guanajuato")
        public String location;

        @JsonPropertyDescription("The temperature unit, can be 'celsius' or 'fahrenheit'")
        @JsonProperty(required = true)
        public WeatherUnit unit;
    }

    public enum WeatherUnit {
        CELSIUS, FAHRENHEIT;
    }

    public static class WeatherResponse {
        public String location;
        public WeatherUnit unit;
        public int temperature;
        public String description;

        public WeatherResponse(String location, WeatherUnit unit, int temperature, String description) {
            this.location = location;
            this.unit = unit;
            this.temperature = temperature;
            this.description = description;
        }
    }

    public void example(String place) {
        String token = System.getenv("OPENAI_API_KEY");
        OpenAiService service = new OpenAiService(token);

        FunctionExecutor functionExecutor = new FunctionExecutor(
                Collections.singletonList(
                        ChatFunction.builder()
                                .name("get_weather")
                                .description("Get the current weather of a location")
                                .executor(Weather.class, w -> new WeatherResponse(w.location, w.unit, new Random().nextInt(50), "sunny"))
                                .build()
                )
        );

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
        messages.add(systemMessage);

        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), place);
        messages.add(firstMsg);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(messages)
                .functions(functionExecutor.getFunctions())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .n(1)
                .maxTokens(100)
                .logitBias(new HashMap<>())
                .build();
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        messages.add(responseMessage);

        ChatFunctionCall functionCall = responseMessage.getFunctionCall();
        if (functionCall != null) {
            log.info("Trying to execute {}...", functionCall.getName());
            Optional<ChatMessage> message = functionExecutor.executeAndConvertToMessageSafely(functionCall);
            if (message.isPresent()) {
                log.info("Executed {}", functionCall.getName());
                messages.add(message.get());
            } else {
                log.error("Something went wrong with the execution of {}", functionCall.getName());
            }
        }

        for (ChatMessage chatMessage : messages) {
            log.info(chatMessage.toString());
        }
    }
}
