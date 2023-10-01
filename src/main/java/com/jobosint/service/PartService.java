package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.event.PersistPartEvent;
import com.jobosint.model.Part;
import com.jobosint.parser.OemPartsOnlinePageParser;
import com.jobosint.parser.ParseResult;
import com.jobosint.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@RequiredArgsConstructor
@Slf4j
@Service
public class PartService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PartRepository partRepository;
    private final OemPartsOnlinePageParser oemPartsOnlinePageParser;
    private final HttpClientFactory httpClientFactory;

    @Transactional
    public Part savePart(Part part) {
        log.info("Saving part: {}", part);
        var persistedPage = partRepository.save(part);
        return persistedPage;
    }

    public void refresh() throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline");
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<Part>> result = oemPartsOnlinePageParser.parse(path);
                List<Part> parts = result.getData();
                if (parts != null) {
                    parts.forEach(part -> {
                        applicationEventPublisher.publishEvent(new PersistPartEvent(this, part));
                    });
                } else {
                    log.warn("No parts found for: {}", path);
                }
            });
        }
    }

    public void downloadOemPartsOnlineImage() {

    }

    public void downloadPartSouqImage(String partNumber) {

        String imagesDir = "/home/shane/projects/jobosint/images/partsouq";

        String partNumberNoDash = partNumber.replaceAll("-", "");
        String url = "https://partsouq.com/assets/tesseract/assets/partsimages/Toyota/" + partNumberNoDash + ".jpg";

        int slashPos = url.lastIndexOf('/');
        String fileName = url.substring(slashPos + 1);
        Path localFilePath = Paths.get(imagesDir, fileName);
        File localFile = localFilePath.toFile();
        if (localFile.exists()) {
            log.info("Found local file: {}; skipping download", localFile.getAbsolutePath());
//            return Optional.of(localFilePath);
        }

        log.info("Fetching image: {}", url);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "*/*")
                .GET().build();

        HttpResponse<Path> response;
        try {
            response = httpClientFactory.getClient().send(request, HttpResponse.BodyHandlers.ofFile(localFilePath, CREATE, WRITE));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() > 400) {
            log.error("status: {}, url: {}", response.statusCode(), url);
//            return Optional.empty();
        }
//        return Optional.of(localFilePath);
    }
// C5UHiRRUbFLng18s

    // https://dz310nzuyimx0.cloudfront.net/strapr1/0decf54629fab771848aee08a73f1751/f12e75021373fd6cc160e2b79ef4b40e.png
    // https://dz310nzuyimx0.cloudfront.net/strapr1/2c1033063381cb4a59e0bb7b12c7cb20/377fe4315aa2d05664fb2fcc3e8d907a.png
}
