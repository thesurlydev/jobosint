package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.model.Part;
import com.jobosint.model.Price;
import com.jobosint.model.Vendor;
import com.jobosint.model.VendorPart;
import com.jobosint.parse.*;
import com.jobosint.repository.PartRepository;
import com.jobosint.repository.PriceRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Service
public class PartService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PartRepository partRepository;
    private final PriceRespository priceRespository;
    private final OemPartsOnlinePageParser oemPartsOnlinePageParser;
    private final ToyotaPartsDealPageParser toyotaPartsDealPageParser;
    private final CruiserCorpsProductParser cruiserCorpsProductParser;
    private final CruiserYardPageParser cruiserYardPageParser;
    private final HttpClientFactory httpClientFactory;

    public List<Part> getAllParts() {
        return partRepository.findAllPartsOrderByCategorySubCategoryTitle();
    }

    public Optional<Part> getPartByPartNumber(String partNumber) {
        return partRepository.findPartByPartNumber(partNumber);
    }

    //    @Transactional
    public Part savePart(Part part) throws DuplicateKeyException {
        Optional<Part> foundPart = getPartByPartNumber(part.partNumber());
        return foundPart.orElseGet(() -> partRepository.save(part));
    }

    public void refresh() throws IOException {

        // merge all the parts
        List<VendorPart> allResults = new ArrayList<>();
        allResults.addAll(getOemPartsOnlineParts());
        allResults.addAll(getToyotaPartsDealParts());

        // now key on part number
        Map<String, List<VendorPart>> vendorPartMap = new HashMap<>();

        for (VendorPart vendorPart : allResults) {
            String partNumber = vendorPart.part().partNumber();
            if (vendorPartMap.containsKey(partNumber)) {
                vendorPartMap.get(partNumber).add(vendorPart);
            } else {
                List<VendorPart> vendorParts = new ArrayList<>();
                vendorParts.add(vendorPart);
                vendorPartMap.put(partNumber, vendorParts);
            }
        }

        List<Part> parts = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        vendorPartMap.forEach((partNumber, vendorParts) -> {
            if (vendorParts.size() == 1) {
                VendorPart vendorPart = vendorParts.getFirst();
                Part part = vendorPart.part();
                parts.add(part);

                Price price = vendorPart.priceObj();
                if (isValidPrice(price.price())) {
                    prices.add(price);
                }
            } else {
                // save part from OEM_PARTS_ONLINE
                Optional<VendorPart> maybeOemPartsOnlinePart = vendorParts.stream()
                        .filter(vendorPart -> vendorPart.vendor() == Vendor.OEM_PARTS_ONLINE)
                        .findFirst();
                maybeOemPartsOnlinePart.ifPresent(vendorPart -> parts.add(vendorPart.part()));
                vendorParts.stream()
                        .map(VendorPart::priceObj)
                        .filter(price -> isValidPrice(price.price()))
                        .forEach(prices::add);
            }
        });

        partRepository.saveAll(parts);
        log.info("Saved {} parts", parts.size());
        priceRespository.saveAll(prices);
        log.info("Saved {} prices", prices.size());
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
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

    public List<VendorPart> getToyotaPartsDealParts() throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/toyotapartsdeal");
        AtomicInteger filesProcessed = new AtomicInteger();
        int totalFiles = Objects.requireNonNull(dirPath.toFile().listFiles()).length;
        Map<String, VendorPart> allResultsMap = new HashMap<>();
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<VendorPart>> result = toyotaPartsDealPageParser.parse(path);
                result.getData().forEach(vendorPart -> {
                    String partNumber = vendorPart.part().partNumber();
                    allResultsMap.put(partNumber, vendorPart);
                });
                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }
        return allResultsMap.values().stream().toList();
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
     */

    public List<VendorPart> getOemPartsOnlineParts() throws IOException {
        Path dirPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline");
        AtomicInteger filesProcessed = new AtomicInteger();
        int totalFiles = Objects.requireNonNull(dirPath.toFile().listFiles()).length;
        Map<String, VendorPart> allResultsMap = new HashMap<>();
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.forEach(path -> {
                ParseResult<List<VendorPart>> result = oemPartsOnlinePageParser.parse(path);
                result.getData().forEach(vendorPart -> {
                    String partNumber = vendorPart.part().partNumber();
                    allResultsMap.put(partNumber, vendorPart);
                });
                filesProcessed.addAndGet(1);
                log.info("Processed {} of {} files", filesProcessed, totalFiles);
            });
        }
        return allResultsMap.values().stream().toList();
    }


}
