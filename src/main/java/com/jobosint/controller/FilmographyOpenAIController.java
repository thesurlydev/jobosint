package com.jobosint.controller;

import com.jobosint.model.ai.ActorsFilms;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class FilmographyOpenAIController {

    private final ChatClient chatClient;

    @GetMapping("/ai/filmography")
    public ActorsFilms generate(@RequestParam(value = "actor", defaultValue = "Jeff Bridges") String actor) {
        var outputParser = new BeanOutputParser<>(ActorsFilms.class);

        String userMessage =
                """
                Generate the filmography for the actor {actor}.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor, "format", outputParser.getFormat() ));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();

        return outputParser.parse(generation.getOutput().getContent());
    }
}