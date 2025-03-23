package com.jobosint.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class PgVectorStoreConfig {

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public PgVectorStore jobVectorStore() {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .schemaName("job")
                .dimensions(1536)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .maxDocumentBatchSize(8000)
                .build();
    }

    @Bean
    public PgVectorStore resumeVectorStore() {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .schemaName("resume")
                .dimensions(1536)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .maxDocumentBatchSize(8000)
                .build();
    }

}
