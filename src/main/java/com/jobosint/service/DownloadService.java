package com.jobosint.service;

import com.jobosint.client.HttpClientFactory;
import com.jobosint.model.DownloadImageRequest;
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
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final HttpClientFactory httpClientFactory;

    public void downloadImage(DownloadImageRequest downloadImageRequest) {
        String url = downloadImageRequest.url();
        Path localFilePath = downloadImageRequest.targetDir().resolve(downloadImageRequest.targetFilename());

        if (!downloadImageRequest.targetDir().toFile().exists()) {
            try {
                Files.createDirectories(downloadImageRequest.targetDir());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        log.info("Downloading {} to {}", url, localFilePath);
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
        } else {
            log.info("status: {}, url: {}", response.statusCode(), url);
        }
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
