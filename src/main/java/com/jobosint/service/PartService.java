package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.event.DownloadImageEvent;
import com.jobosint.event.PersistPartEvent;
import com.jobosint.model.DownloadImageRequest;
import com.jobosint.model.JobDetail;
import com.jobosint.model.Part;
import com.jobosint.parser.OemPartsOnlinePageParser;
import com.jobosint.parser.ParseResult;
import com.jobosint.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Service
public class PartService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PartRepository partRepository;
    private final OemPartsOnlinePageParser oemPartsOnlinePageParser;
    private final HttpClientFactory httpClientFactory;

    public List<Part> getAllParts() {
        return partRepository.findAllPartsOrderByTitle();
    }

    @Transactional
    public Part savePart(Part part) {
        log.info("Saving part: {}", part);
        return partRepository.save(part);
    }

    public void refresh(boolean persistParts, boolean downloadImages) throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline");
        Path targetImageDir = Path.of("/home/shane/projects/jobosint/images/oempartsonline");
        AtomicInteger filesProcessed = new AtomicInteger();
        int totalFiles = Objects.requireNonNull(dirPath.toFile().listFiles()).length;
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<Part>> result = oemPartsOnlinePageParser.parse(path);
                List<Part> parts = result.getData();
                if (parts != null) {
                    parts.forEach(part -> {
                        if (persistParts) {
                            applicationEventPublisher.publishEvent(new PersistPartEvent(this, part));
                        }
                        if (downloadImages) {
                            String refImage = part.refImage();
                            if (!refImage.equals("//s3.amazonaws.com/static.revolutionparts.com/assets/images/toyota.png")) {
                                if (refImage.startsWith("//dz310nzuyimx0.cloudfront.net/strapr1/")) {
                                    String targetFilename = refImage.substring("//dz310nzuyimx0.cloudfront.net/strapr1/".length() + 1);
                                    Path localPath = Paths.get(targetImageDir.toString(), targetFilename);
                                    if (!localPath.toFile().exists()) {
                                        String[] imageFileParts = targetFilename.split("/");
                                        Path targetDir = targetImageDir.resolve(imageFileParts[0]);
                                        String filename = imageFileParts[1];
                                        applicationEventPublisher.publishEvent(new DownloadImageEvent(this, new DownloadImageRequest("https:" + refImage, targetDir, filename, false)));
                                    } else {
                                        log.info("{} already exists; skipping download", localPath);
                                    }
                                } else {
                                    log.warn("Unknown image source: {}", part.refImage());
                                }
                            }
                        }
                    });
                } else {
                    log.warn("No parts found for: {}", path);
                }
                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }
    }


}