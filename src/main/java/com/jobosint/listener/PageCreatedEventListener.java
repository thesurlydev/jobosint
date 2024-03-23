package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.Page;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.parse.BuiltinParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import com.jobosint.service.ai.CompanyDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final CompanyDetailsService companyDetailsService;
    private final JobService jobService;
    private final LinkedInParser linkedInParser;
    private final BuiltinParser builtInParser;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        Page page = event.getPage();
        String contentPath = page.contentPath();
        String jobSource;

        JobDescriptionParserResult jobDescriptionParserResult = null;
        try {

            if (page.url().startsWith("https://builtin.com/job/")) {
                jobDescriptionParserResult = builtInParser.parseJobDescription(contentPath);
                jobSource = "Builtin";
            } else if (page.url().startsWith("https://www.linkedin.com/jobs/view/")) {
                jobDescriptionParserResult = linkedInParser.parseJobDescription(contentPath);
                jobSource = "LinkedIn";
            } else {
                log.error("Unsupported job site: {}", page.url());
                return;
            }
            if (jobDescriptionParserResult != null) {
                Company company = processCompany(jobDescriptionParserResult);
                if (company != null) {
                    Job job = processJob(jobDescriptionParserResult, page, company, jobSource);
                }
            }
        } catch (IOException e) {
            log.error("Failed to parse job description: {}", contentPath, e);
        }
    }

    private Company processCompany(JobDescriptionParserResult jobDescriptionParserResult) {
        String companyName = jobDescriptionParserResult.companyName();
        Company company = null;
        if (companyName.isBlank()) {
            log.warn("Using N/A for company");
            List<Company> companies = companyService.search("N/A");
            company = companies.getFirst();
        } else {
            List<Company> companies = companyService.search(companyName);
            if (companies.isEmpty()) {
                // use companydetailservice to get company details
                CompanyDetail detail = companyDetailsService.getCompanyDetails(companyName);
                Company c = new Company(null, companyName, detail.websiteLink(), detail.stockTicker(), detail.numberOfEmployees(), detail.summary(), detail.location());
                log.info("Creating new company: {}", c);
                company = companyService.saveCompany(c);
            } else if (companies.size() == 1) {
                company = companies.getFirst();
            } else {
                log.error("Ambiguous company name: {}", companyName);
            }
        }
        return company;
    }

    private Job processJob(JobDescriptionParserResult jobDescriptionParserResult,
                            Page page,
                            Company company,
                            String jobSource) {

        Job job = new Job(null, company.id(), jobDescriptionParserResult.title(), page.url(), LocalDateTime.now(), null,
                null, jobSource, null, jobDescriptionParserResult.description(), "Active", UUID.fromString(page.id()));
        return jobService.saveJob(job);
    }
}
