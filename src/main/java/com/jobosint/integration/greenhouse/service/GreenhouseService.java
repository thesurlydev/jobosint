package com.jobosint.integration.greenhouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.integration.greenhouse.GreenhouseTokenLoader;
import com.jobosint.integration.greenhouse.config.GreenhouseConfig;
import com.jobosint.integration.greenhouse.model.GetJobResult;
import com.jobosint.integration.greenhouse.model.GreenhouseJobsResponse;
import com.jobosint.integration.greenhouse.model.Job;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class GreenhouseService {

    private final GreenhouseConfig greenhouseConfig;
    private final GreenhouseTokenLoader greenhouseTokenLoader;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    private static final Set<String> titleIncludes = Set.of("engineer");
    private static final Set<String> titleExcludes = Set.of("manager");

    public void fetchJobs() throws IOException {
        if (!greenhouseConfig.getFetchJobsEnabled()) {
            log.info("Greenhouse fetch jobs disabled; skipping");
            return;
        }
        greenhouseTokenLoader.getGreenhouseTokens()
                .forEach(boardToken -> {
                    log.info("Fetching jobs for: {}", boardToken);
                    File boardDir = createBoardDir(boardToken);
                    GreenhouseJobsResponse greenhouseJobsResponse = null;
                    try {
                        greenhouseJobsResponse = getJobList(boardToken);
                    } catch (Exception e) {
                        log.error("Failed to get job list for board token: {}", boardToken, e);
                    }
                    if (greenhouseJobsResponse == null) {
                        log.warn("GreenhouseJobsResponse was null");
                        return;
                    }
                    Integer totalJobs = greenhouseJobsResponse.meta().total();
                    log.info("Found {} total jobs", totalJobs);
                    List<GetJobResult> jobResults = greenhouseJobsResponse.jobs().stream()
                            .filter(job -> titleIncludes.stream().anyMatch(include -> job.title().toLowerCase().contains(include)))
                            .filter(job -> titleExcludes.stream().noneMatch(exclude -> job.title().toLowerCase().contains(exclude)))
                            .map(job -> {
                                try {
                                    return getJob(boardToken, job.id().toString());
                                } catch (Exception e) {
                                    log.error("Failed to get job: {}", job.id(), e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .toList();
                            /*.forEach(job -> {
                                if (greenhouseConfig.getSaveToFileEnabled()) {
                                    saveToFile(job, boardDir);
                                }
                                if (greenhouseConfig.getSaveToDbEnabled()) {
                                    saveToDb(job);
                                }
                            });*/
                    if (greenhouseConfig.getSaveToFileEnabled()) {
                        jobResults.forEach(job -> saveToFile(job, boardDir));
                    }
                    if (greenhouseConfig.getSaveToDbEnabled()) {
                        saveJobToDb(jobResults);
                    }
                });
    }

    private Company saveCompanyToDb() {
        // TODO
        return null;
    }

    private void saveJobToDb(List<GetJobResult> getJobResults) {
        // TODO
    }

    private void saveToFile(GetJobResult job, File boardDir) {
        try {
            String filename = job.id() + ".json";
            File f = new File(boardDir, filename);
            if (f.exists()) {
                return;
            }
            objectMapper.writeValue(f, job);
        } catch (IOException e) {
            log.error("Error writing job to file", e);
        }
    }

    private @NotNull File createBoardDir(String boardToken) {
        File boardDir = Paths.get(greenhouseConfig.getDownloadDir(), boardToken).toFile();
        if (!boardDir.exists()) {
            boolean success = boardDir.mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create directory: " + boardDir);
            }
            log.info("Created {} directory", boardDir.getAbsolutePath());
        }
        return boardDir;
    }

    public GreenhouseJobsResponse getJobList(String boardToken) {
        String url = greenhouseConfig.getBaseUrl() + boardToken + "/jobs";
        log.info("Fetching job list from: {}", url);
        RestClient client = RestClient.create();
        return client.get()
                .uri(url)
                .retrieve()
                .body(GreenhouseJobsResponse.class);
    }

    public Pair<String, String> getBoardTokenAndIdFromUrl(String greenhouseUrl) {
        var url = greenhouseUrl;
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        // get the board token and job id from the page url
        String[] urlParts = url.split("/");
        String boardToken = urlParts[3];
        String jobId = urlParts[5];

        return Pair.of(boardToken, jobId);
    }

    public GetJobResult getJob(String boardToken, String jobId) {
        String url = greenhouseConfig.getBaseUrl() + boardToken + "/jobs/" + jobId;
        log.info("Fetching job from: {}", url);
        RestClient client = RestClient.create();
        Job job = client.get()
                .uri(url)
                .retrieve()
                .body(Job.class);
        return new GetJobResult(jobId, boardToken, job);
    }

    public com.jobosint.integration.greenhouse.model.Company getCompany(String boardToken) {
        String url = greenhouseConfig.getBaseUrl() + boardToken;
        log.info("Fetching company from: {}", url);
        RestClient client = RestClient.create();
        return client.get()
                .uri(url)
                .retrieve()
                .body(com.jobosint.integration.greenhouse.model.Company.class);
    }
}