package com.jobosint.match;

import com.jobosint.model.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MatchingService {

    private final QdrantVectorStore jobQdrantVectorStore;
    private final QdrantVectorStore resumeQdrantVectorStore;

    public List<Resume> getResumes() {
        SearchRequest resumeSearchRequest = SearchRequest.builder()
                .query("resume")
                .topK(1).build();
        List<Document> resumes = resumeQdrantVectorStore
                .similaritySearch(resumeSearchRequest);

        if (resumes == null || resumes.isEmpty()) {
            log.warn("No resume found");
            return List.of();
        }

        return resumes.stream()
                .map(doc -> new Resume(doc.getId(), (String) doc.getMetadata().get("title"), doc.getText(), null))
                .toList();
    }


    public List<JobMatch> findJobMatchesForResume(int topK) {
        // First, get the resume document
        List<Resume> resumes = getResumes();

        // Then, search for similar jobs based on the resume content
        String resumeText = resumes.getFirst().content();
        if (resumeText == null || resumeText.isEmpty()) {
            log.warn("No text found in resume");
            return List.of();
        }

        String resumeId = resumes.getFirst().id();
        log.info("Finding job matches for resume: {}", resumeId);

        SearchRequest jobSearchRequest = SearchRequest.builder()
                .query(resumeText)
                .topK(topK)
                .build();

        List<Document> matchingJobs = jobQdrantVectorStore.similaritySearch(jobSearchRequest);

        if (matchingJobs == null || matchingJobs.isEmpty()) {
            log.warn("No matching jobs found for resume");
            return List.of();
        }

        // Convert matching jobs to JobMatch objects
        return matchingJobs.stream()
                .map(doc -> new JobMatch(doc.getId(), (String) doc.getMetadata().get("title"), doc.getScore()))
                .toList();
    }

    /*private double calculateSimilarity(String jobDescription, String resume) {
        // Store job description in Qdrant
        Document jobDoc = new Document(jobDescription, new SimpleMetadata(Map.of("type", "job_description")));
        vectorStore.add(List.of(jobDoc));

        // Create resume document with metadata
        SimpleMetadata resumeMetadata = new SimpleMetadata();
        resumeMetadata.add("type", "resume");
        resumeMetadata.add("length", String.valueOf(resume.length()));

        // Query Qdrant for similarity
        SearchRequest searchRequest = SearchRequest.query(resume)
                .withTopK(1)
                .withSimilarityThreshold(0.0f);

        List<Document> results = vectorStore.search(searchRequest);

        if (results.isEmpty()) {
            return 0.0;
        }

        // Get similarity score - normalize to 0-1 range
        double similarity = results.get(0).getScore();

        // Clean up - remove job document from store
        vectorStore.delete(List.of(jobDoc.getId()));

        return similarity;
    }*/
}
