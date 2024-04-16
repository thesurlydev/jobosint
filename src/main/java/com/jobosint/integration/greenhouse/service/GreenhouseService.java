package com.jobosint.integration.greenhouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.integration.greenhouse.config.GreenhouseConfig;
import com.jobosint.integration.greenhouse.model.GetJobResult;
import com.jobosint.integration.greenhouse.model.GreenhouseJobsResponse;
import com.jobosint.integration.greenhouse.model.Job;
import com.jobosint.integration.greenhouse.model.Office;
import com.jobosint.model.Company;
import com.jobosint.service.AttributeService;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import com.jobosint.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class GreenhouseService {

    private final AttributeService attributeService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final GreenhouseConfig greenhouseConfig;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    private static final Set<String> titleIncludes = Set.of("engineer");

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

    public void fetchJobs() throws IOException {
        if (!greenhouseConfig.getFetchJobsEnabled()) {
            log.info("Greenhouse fetch jobs disabled; skipping");
            return;
        }

        List<String> titleExcludes = attributeService.getJobTitleExcludes();

        getGreenhouseTokens()
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
                    // GreenhouseJobsResponse only includes: id, abs_url, title, updated_at
                    List<GetJobResult> jobResults = greenhouseJobsResponse.jobs().stream()
//                            .limit(10)
                            .filter(job -> titleIncludes.stream().anyMatch(include -> job.title().toLowerCase().contains(include)))
                            .filter(job -> titleExcludes.stream().noneMatch(exclude -> job.title().toLowerCase().contains(exclude)))
                            .map(job -> {
                                try {
                                    return getJobResult(boardToken, job.id().toString());
                                } catch (Exception e) {
                                    log.error("Failed to get job: {}", job.id(), e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .filter(this::locationFilter)
                            .toList();

                    log.info("Filtered jobs: {}", jobResults.size());

                    if (greenhouseConfig.getSaveToFileEnabled()) {
                        jobResults.forEach(job -> saveToFile(job, boardDir));
                    }
                    if (greenhouseConfig.getSaveToDbEnabled()) {
                        com.jobosint.integration.greenhouse.model.Company company = getCompany(boardToken);
                        Company c = saveCompanyToDb(company.name(), boardToken);
                        saveJobToDb(c, jobResults);
                    }
                });
    }

    private boolean locationFilter(GetJobResult getJobResult) {
        if (getJobResult == null || getJobResult.job() == null) return true;
        List<Office> offices = getJobResult.job().offices();
        if (offices == null) return true;
        if (!offices.isEmpty()) {
            return offices.stream().anyMatch(office -> office.name().contains("Remote"));
        }
        return false;
    }

    private Company saveCompanyToDb(String companyName, String boardToken) {
        Company companyCandidate = new Company(null, companyName, null, null, null, null, null, null, boardToken);
        return companyService.saveOrMergeCompany(companyName, companyCandidate);
    }

    private void saveJobToDb(Company company, List<GetJobResult> getJobResults) {
        getJobResults.stream()
                .map(jr -> jr.toJob(company.id()))
                .forEach(job -> {
                    Optional<com.jobosint.model.Job> maybeExisting = jobService.getJobByUrl(job.url());
                    if (maybeExisting.isEmpty()) {
                        jobService.saveJob(job);
                    }
                });
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
        return restClient.get()
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

    public GetJobResult getJobResult(String boardToken, String jobId) {

        // first, look for existing record in db
        Optional<GetJobResult> maybeDbGetJobResult = tryGetJobResultDatabase(boardToken, jobId);
        if (maybeDbGetJobResult.isPresent()) {
            log.info("Found db copy of job: {}", jobId);
            return maybeDbGetJobResult.get();
        }

        // second, look for json serialized to disk
        Optional<GetJobResult> maybeLocalGetJobResult = tryGetJobResultLocal(boardToken, jobId);
        if (maybeLocalGetJobResult.isPresent()) {
            log.info("Found local copy of job: {}", jobId);
            return maybeLocalGetJobResult.get();
        }

        // if not found, then make the call to Greenhouse
        String url = greenhouseConfig.getBaseUrl() + boardToken + "/jobs/" + jobId;
        log.info("Fetching job from: {}", url);
        Job job = restClient.get()
                .uri(url)
                .retrieve()
                .body(Job.class);

        // convert job content to markdown
        assert job != null;
        String escapedHtmlContent = job.content();
        String markdownContent = ConversionUtils.convertToMarkdown(escapedHtmlContent);
        Job updatedJob = updateContent(job, markdownContent);

        return new GetJobResult(boardToken, jobId, updatedJob);
    }

    public Job updateContent(Job job, String newContent) {
        return new Job(job.absolute_url(), job.id(), job.title(), newContent, job.updated_at(), job.departments(), job.offices());
    }

    public Optional<GetJobResult> tryGetJobResultDatabase(String boardToken, String jobId) {
        Optional<com.jobosint.model.Job> maybeJob = jobService.getJobBySourceJobId("Greenhouse", jobId);
        return maybeJob.map(job -> GetJobResult.fromJob(boardToken, job));
    }

    public Optional<GetJobResult> tryGetJobResultLocal(String boardToken, String jobId) {
        Path path = Path.of(greenhouseConfig.getDownloadDir(), boardToken, jobId + ".json");
        File file = path.toFile();
        if (file.exists()) {
            try {
                GetJobResult getJobResult = objectMapper.readValue(file, GetJobResult.class);
                Job ej = getJobResult.job();

                // we need to convert the content to markdown
                String markdownContent = ConversionUtils.convertToMarkdown(getJobResult.job().content());
                Job job = new Job(ej.absolute_url(), ej.id(), ej.title(), markdownContent, ej.updated_at(), ej.departments(), ej.offices());

                GetJobResult gjr = new GetJobResult(boardToken, jobId, job);

                return Optional.of(gjr);
            } catch (IOException ioe) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public com.jobosint.integration.greenhouse.model.Company getCompany(String boardToken) {
        String url = greenhouseConfig.getBaseUrl() + boardToken;
        log.info("Fetching company from: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(com.jobosint.integration.greenhouse.model.Company.class);
    }
}