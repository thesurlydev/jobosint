package com.jobosint.ingest;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MarkdownDocumentReader {

    private final MarkdownDocumentReaderConfig.Builder markdownDocumentReaderConfigBuilder;

    public List<Document> read(String markdownContent) {
        byte[] markdownContentBytes = markdownContent.getBytes(StandardCharsets.UTF_8);
        Resource resource = new ByteArrayResource(markdownContentBytes);
        MarkdownDocumentReaderConfig markdownDocumentReaderConfig = markdownDocumentReaderConfigBuilder
                .build();
        org.springframework.ai.reader.markdown.MarkdownDocumentReader markdownDocumentReader
                = new org.springframework.ai.reader.markdown.MarkdownDocumentReader(resource, markdownDocumentReaderConfig);
        return markdownDocumentReader.read();
    }
}
