package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.event.PersistVendorPartEvent;
import com.jobosint.model.VendorPart;
import com.jobosint.model.Part;
import com.jobosint.parse.*;
import com.jobosint.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final ToyotaPartsDealPageParser toyotaPartsDealPageParser;
    private final CruiserCorpsProductParser cruiserCorpsProductParser;
    private final CruiserYardPageParser cruiserYardPageParser;
    private final HttpClientFactory httpClientFactory;

    public List<Part> getAllParts() {
        return partRepository.findAllPartsOrderByTitle();
    }

    //    @Transactional
    public Part savePart(Part part) {

        log.info("Saving part: {}", part);
        Part pp = null;
        try {
            pp = partRepository.save(part);
        } catch (Exception e) {
            if (e.getCause() instanceof DuplicateKeyException) {
                log.warn("Duplicate: {}", part);
            } else {
                log.error("Error saving part", e);
            }
        }
        return pp;
    }

    public void refresh(boolean persistParts) throws IOException {
        if (persistParts) {
            log.warn("Deleting all part records");
//            partRepository.deleteAll();
        }
//        refreshCruiserCorps(persistParts, downloadImages);
//        refreshOemPartsOnline(persistParts, downloadImages);
        refreshToyotaPartsDeal(persistParts);
    }

    public void refreshCruiserYard(boolean persistParts, boolean downloadImages) throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/cruiseryard");
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<String>> result = cruiserYardPageParser.getDetailPageUrls(path);
                List<String> detailPageUrls = result.getData();
                if (detailPageUrls != null) {
                    // TODO download detail pages

                } else {
                    log.warn("No parts found for: {}", path);
                }


            });
        }
    }

    public void refreshToyotaPartsDeal(boolean persistParts) throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/toyotapartsdeal");
        AtomicInteger filesProcessed = new AtomicInteger();
        int totalFiles = Objects.requireNonNull(dirPath.toFile().listFiles()).length;
        AtomicInteger totalParts = new AtomicInteger();
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<VendorPart>> result = toyotaPartsDealPageParser.parse(path);
                List<VendorPart> parts = result.getData();
                if (parts != null) {
                    totalParts.addAndGet(parts.size());
                    parts.forEach(part -> {
                        if (persistParts) {
                            applicationEventPublisher.publishEvent(new PersistVendorPartEvent(this, part));
                        }
                    });
                } else {
                    log.warn("No parts found for: {}", path);
                }

                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }

        log.info("Found {} parts", totalParts);
    }

    /*public void refreshCruiserCorps(boolean persistParts, boolean downloadImages) throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/cruisercorps");
        AtomicInteger filesProcessed = new AtomicInteger();
        int totalFiles = Objects.requireNonNull(dirPath.toFile().listFiles()).length;
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<Part> result = cruiserCorpsProductParser.parse(path);
                Part part = result.getData();
                if (part != null) {
                    if (persistParts) {
                        applicationEventPublisher.publishEvent(new PersistPartEvent(this, part));
                    }
                    if (downloadImages) {
                        // todo
                    }

                } else {
                    log.warn("No parts found for: {}", path);
                }
                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }
    }

    public void refreshOemPartsOnline(boolean persistParts, boolean downloadImages) throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline");
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
                    });
                } else {
                    log.warn("No parts found for: {}", path);
                }
                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }
    }*/


}
