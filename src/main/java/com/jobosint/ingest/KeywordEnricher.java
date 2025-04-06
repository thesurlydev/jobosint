package com.jobosint.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordEnricher {
    private final ChatModel chatModel;

    public List<Document> enrich(List<Document> documents, Integer keywordCount) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.chatModel, keywordCount);
        return enricher.apply(documents);
    }
}
