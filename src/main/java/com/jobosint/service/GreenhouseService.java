package com.jobosint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jobosint.model.greenhouse.GetJobResult;
import com.jobosint.model.greenhouse.GreenhouseJobsResponse;
import com.jobosint.model.greenhouse.Job;
import com.jobosint.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GreenhouseService {

    private static final String DOWNLOAD_DIR = "downloads/greenhouse";
    private static final String BASE_URL = "https://api.greenhouse.io/v1/boards/";
    // https://developers.greenhouse.io/job-board.html

    private static final Set<String> includes = Set.of("engineer");
    private static final Set<String> excludes = Set.of("manager");

    public void downloadJobs() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(new JavaTimeModule());
        Files.readAllLines(Paths.get("data/reference/greenhouse-board-tokens.txt"))
                .forEach(boardToken -> {
                    System.out.println("Board token: " + boardToken);
                    File boardDir = Paths.get(DOWNLOAD_DIR, boardToken).toFile();
                    if (!boardDir.exists()) {
                        boolean success = boardDir.mkdirs();
                        if (!success) {
                            throw new RuntimeException("Failed to create directory: " + boardDir);
                        }
                    }
                    GreenhouseJobsResponse response = null;
                    try {
                        response = getJobList(boardToken);
                    } catch (Exception e) {
                        System.out.println("Failed to get job list for board token: " + boardToken);
                    }
                    if (response == null) {
                        return;
                    }
                    response.jobs().stream()
                            .filter(job -> includes.stream().anyMatch(include -> job.title().toLowerCase().contains(include)))
                            .filter(job -> excludes.stream().noneMatch(exclude -> job.title().toLowerCase().contains(exclude)))
                            .map(job -> {
                                try {
                                    return getJob(boardToken, job.id().toString());
                                } catch (Exception e) {
                                    System.out.println("Failed to get job: " + job.id());
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .forEach(job -> {
                                try {
                                    String filename = StringUtils.slugify(job.job().title()) + "-" + job.id() + ".json";
                                    File f = new File(boardDir, filename);
                                    if (f.exists()) {
                                        return;
                                    }
                                    mapper.writeValue(f, job);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }

    public GreenhouseJobsResponse getJobList(String boardToken) {
        RestClient client = RestClient.create();
        return client.get()
                .uri(BASE_URL + boardToken + "/jobs")
                .retrieve()
                .body(GreenhouseJobsResponse.class);
    }

    public GetJobResult getJob(String greenhouseUrl) {
        var url = greenhouseUrl;
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        // get the board token and job id from the page url
        String[] urlParts = url.split("/");
        String boardToken = urlParts[3];
        String jobId = urlParts[5];

        return getJob(boardToken, jobId);
    }

    public GetJobResult getJob(String boardToken, String jobId) {
        RestClient client = RestClient.create();
        Job job = client.get()
                .uri(BASE_URL + boardToken + "/jobs/" + jobId)
                .retrieve()
                .body(Job.class);
        return new GetJobResult(jobId, boardToken, job);
    }
}