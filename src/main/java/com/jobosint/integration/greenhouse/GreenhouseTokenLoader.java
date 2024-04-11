package com.jobosint.integration.greenhouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class GreenhouseTokenLoader {
    @Value("classpath:data/greenhouse-board-tokens.txt")
    private Resource greenhouseTokensFileResource;

    public List<String> getGreenhouseTokens() {
        try (Stream<String> lines = Files.lines(Paths.get(greenhouseTokensFileResource.getURI()))) {
            List<String> tokens = lines.toList();
            log.info("Loaded {} Greenhouse board tokens", tokens.size());
            return tokens;
        } catch (IOException e) {
            throw new RuntimeException("Error reading greenhouse tokens", e);
        }
    }
}
