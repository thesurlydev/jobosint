package com.jobosint.ingest;

import com.jobosint.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private final JobService jobService;
//    private final MarkdownDocumentReader markdownDocumentReader;



    public void ingestJob(UUID jobId) {
        log.info("Ingesting job: " + jobId);

        jobService.getJobDetail(jobId).ifPresent(jd ->{
            String jobContent = jd.job().content();

            byte[] markdownContentBytes = jobContent.getBytes(StandardCharsets.UTF_8);
            Resource resource = new ByteArrayResource(markdownContentBytes);

            TextReader textReader = new TextReader(resource);
            List<Document> documents = textReader.get();
            List<Document> splitDocuments = textSplitter.splitDocuments(documents);
            List<Document> enrichedDocuments = new JobEnricher(jd).apply(splitDocuments);
            List<Document> documentsToSave = updateJobDocumentIds(jobId, enrichedDocuments);
            jobQdrantVectorStore.add(documentsToSave);
            log.info("Added {} documents to job vector store", documentsToSave.size());
        });
    }

    public void ingestResume(String path) {
        log.info("Ingesting resume from path: " + path);
        List<Document> documents = pdfDocumentReader.read(path);
        List<Document> splitDocuments = textSplitter.splitDocuments(documents);
        List<Document> enrichedDocuments = keywordEnricher.enrich(splitDocuments, 10);
        List<Document> documentsToSave = updateResumeDocumentIds(enrichedDocuments);
        resumeQdrantVectorStore.add(documentsToSave);
        log.info("Added documents to resume vector store");
    }

    private List<Document> updateJobDocumentIds(UUID jobId, List<Document> documents) {
        List<Document> out = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            String id = jobId.toString() + "-" + i;
            String uuid = UUID.nameUUIDFromBytes(id.getBytes()).toString();
            Document newDocument = new Document(uuid, Objects.requireNonNull(document.getText()), document.getMetadata());
            out.add(newDocument);
        }
        return out;
    }

    private List<Document> updateResumeDocumentIds(List<Document> documents) {
        return documents.stream()
                .map(document -> {
                    String filePageId = document.getMetadata().get("file_name") + "-" + document.getMetadata().get("page_number");
                    String uuid = UUID.nameUUIDFromBytes(filePageId.getBytes()).toString();
                    return new Document(uuid, Objects.requireNonNull(document.getText()), document.getMetadata());
                }).toList();
    }
}
