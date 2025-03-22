package com.jobosint.service;

import com.jobosint.ingest.PdfDocumentReader;
import com.jobosint.ingest.TextSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IngestService {

    private final PdfDocumentReader pdfDocumentReader;
    private final TextSplitter textSplitter;

    public void ingest(String path) {

        log.info("Ingesting documents from path: " + path);

        List<Document> documents = pdfDocumentReader.read(path);

        log.info("Read documents: " + documents.size());


        List<Document> splitDocuments = textSplitter.splitDocuments(documents);

        log.info("Split documents: " + splitDocuments.size());
    }
}
