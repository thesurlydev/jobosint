package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final HttpClientFactory httpClientFactory;

    /**
     * Given a path to a local file, download all links in the file to a local directory
     *
     * @param path
     * @return
     * @throws IOException
     */
    public List<Optional<Path>> downloadAll(Path path) throws IOException {
        List<String> urls = Files.readAllLines(path);
        return urls.stream().map(url -> {
            try {
                return download(url);
            } catch (InterruptedException | URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public Optional<Path> download(String url) throws InterruptedException, URISyntaxException, IOException {

        int slashPos = url.lastIndexOf('/');
        String path = url.substring(slashPos + 1);
        File localFile = new File(path + ".html");
        Path localFilePath = localFile.toPath();
        if (localFile.exists()) {
            return Optional.of(localFilePath);
        }

        Thread.sleep(1000);

        log.info("Downloading: {}", url);

        URI uri = new URI(url);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = httpClientFactory.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() > 399) {
            log.error("status: {}, url: {}", response.statusCode(), url);
            return Optional.empty();
        }

        Files.writeString(localFilePath, response.body());
        log.info("Wrote {}", localFile.getCanonicalPath());

        return Optional.of(localFilePath);
    }
}
