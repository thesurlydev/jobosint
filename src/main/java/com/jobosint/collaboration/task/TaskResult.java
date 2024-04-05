package com.jobosint.collaboration.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jobosint.util.ConversionUtils;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class TaskResult {
    private final Object data;
    private final String dataType;

    public TaskResult(Object data) {
        this.data = data;
        this.dataType = data.getClass().getName();
    }

    public String display() {
        if (data instanceof String raw) {
            var markdown = ConversionUtils.convertToMarkdown(raw);
            return ConversionUtils.convertToHtml(markdown);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty print
            String prettyJson;
            try {
                prettyJson = mapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return prettyJson;
        }
    }
}
