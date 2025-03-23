package com.jobosint.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestService {

    private final KeywordEnricher keywordEnricher;
    private final PdfDocumentReader pdfDocumentReader;
    private final TextSplitter textSplitter;
//    private final PgVectorStore jobVectorStore;
//    private final PgVectorStore resumeVectorStore;
    private final QdrantVectorStore jobQdrantVectorStore;
    private final QdrantVectorStore resumeQdrantVectorStore;

    /*public void ingestJob(UUID jobId) {
        log.info("Ingesting job: " + jobId);
        List<Document> documents = pdfDocumentReader.read(path);
        List<Document> splitDocuments = textSplitter.splitDocuments(documents);
        List<Document> enrichedDocuments = keywordEnricher.enrich(splitDocuments, 5);
        List<Document> documentsToSave = updateDocumentIds(enrichedDocuments);
        jobQdrantVectorStore.add(documentsToSave);
        log.info("Added documents to job vector store");
    }*/

    public void ingestResume(String path) {
        log.info("Ingesting resume from path: " + path);
        List<Document> documents = pdfDocumentReader.read(path);
        List<Document> splitDocuments = textSplitter.splitDocuments(documents);
        List<Document> enrichedDocuments = keywordEnricher.enrich(splitDocuments, 5);
        List<Document> documentsToSave = updateDocumentIds(enrichedDocuments);
        resumeQdrantVectorStore.add(documentsToSave);
        log.info("Added documents to resume vector store");
    }

    private List<Document> updateDocumentIds(List<Document> documents) {
        return documents.stream()
                .map(document -> {
                    String filePageId = document.getMetadata().get("file_name") + "-" + document.getMetadata().get("page_number");
                    String uuid = UUID.nameUUIDFromBytes(filePageId.getBytes()).toString();
                    return new Document(uuid, Objects.requireNonNull(document.getText()), document.getMetadata());
                }).toList();
    }
}
