package com.jobosint.collaboration.agent.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.jobosint.collaboration.agent.AgentService;
import com.jobosint.collaboration.annotation.Agent;
import com.jobosint.collaboration.annotation.Tool;

import java.lang.reflect.Type;

@Agent(goal = "Interpret and write software code")
public class SoftwareEngineer extends AgentService {
    @Tool(name = "SchemaGenerator", description = "Given an instance of a bean, generate a JSON schema")
    public String generateSchema(Object obj) {
        Class<?> clazz = obj.getClass();
        return generateSchema(clazz);
    }

    /*
    Stolen from BeanOutputParser
     */
    private String generateSchema(Class<?> clazz) {

        JacksonModule jacksonModule = new JacksonModule();
        SchemaGeneratorConfigBuilder configBuilder =
                (new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON))
                        .with(jacksonModule);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonNode = generator.generateSchema(clazz, new Type[0]);
        ObjectWriter objectWriter = (new ObjectMapper())
                .writer((new DefaultPrettyPrinter())
                        .withObjectIndenter((new DefaultIndenter())
                                .withLinefeed(System.lineSeparator())));

        String jsonSchema;
        try {
            jsonSchema = objectWriter.writeValueAsString(jsonNode);
        } catch (JsonProcessingException var8) {
            JsonProcessingException e = var8;
            throw new RuntimeException("Could not pretty print json schema for " + clazz, e);
        }
        return String.format("\n```%s```\n", jsonSchema);
    }
}
