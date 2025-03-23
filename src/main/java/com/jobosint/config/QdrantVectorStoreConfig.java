package com.jobosint.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantVectorStoreConfig {

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient grpcClient = QdrantGrpcClient
                .newBuilder("localhost", 6334, false)
                .build();
        return new QdrantClient(grpcClient);
    }

    @Bean
    public QdrantVectorStore jobQdrantVectorStore(QdrantClient qdrantClient,
                                                  EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("job")     // Optional: defaults to "vector_store"
                .initializeSchema(false)                  // Optional: defaults to false
                .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
                .build();
    }


    /**
     * NOTE: You need to manually create the schema in Qdrant before initializing the vector store.
     *
     * @param qdrantClient
     * @param embeddingModel
     * @return
     */

    @Bean
    public QdrantVectorStore resumeQdrantVectorStore(QdrantClient qdrantClient,
                                                     EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("resume")     // Optional: defaults to "vector_store"
                .initializeSchema(false)                  // Optional: defaults to false
                .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
                .build();
    }
}
