package com.jobosint.service;

import com.github.slugify.Slugify;
import com.jobosint.config.AppConfig;
import com.jobosint.model.Page;
import com.jobosint.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class PageService {
    private final AppConfig appConfig;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PageRepository pageRepository;

    final Slugify slugify = Slugify.builder().build();

    @Transactional
    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

    public Path saveContent(String url, String content) throws IOException {
        // Validate content is neither null nor blank
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or blank.");
        }

        // Generate file slug based on URL or UUID
        String fileSlug = (url == null || url.trim().isEmpty())
                ? UUID.randomUUID().toString()
                : slugify.slugify(url.trim());

        // Determine the directory for today's date
        Path contentDir = appConfig.pageDir()
                .resolve(OffsetDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        // Safely create directories if they don't exist
        try {
            Files.createDirectories(contentDir);
        } catch (IOException e) {
            throw new IOException("Failed to create directory structure for content storage.", e);
        }

        // Resolve the file path and write content
        Path filePath = contentDir.resolve(fileSlug + ".html");
        try {
            return Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new IOException("Failed to write content to file: " + filePath, e);
        }
    }
}
