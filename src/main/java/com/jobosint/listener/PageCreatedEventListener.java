package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.*;
import com.jobosint.model.ai.JobDescriptionParseResult;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.parse.ParseResult;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import com.jobosint.service.PageService;
import com.jobosint.service.ai.CompanyDetailsService;
import com.jobosint.service.ai.JobDescriptionParserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final CompanyDetailsService companyDetailsService;
    private final PageService pageService;
    private final JobService jobService;
    private final JobDescriptionParser jobDescriptionParser;
//    private final JobDescriptionParserService jobDescriptionParserService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LinkedInParser linkedInParser;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        Page page = event.getPage();
        String contentPath = page.contentPath();

        LinkedInJobDescription linkedInJobDescription;
        try {
            linkedInJobDescription = linkedInParser.parseJobDescription(contentPath);
        } catch (IOException e) {
            log.error("Failed to parse job description: {}", contentPath, e);
            return;
        }

        String companyName = linkedInJobDescription.companyName();

        Company company;
        if (companyName.isBlank()) {
            log.warn("Using N/A for company");
            List<Company> companies = companyService.search("N/A");
            company = companies.getFirst();
        } else {
            List<Company> companies = companyService.search(companyName);
            if (companies.isEmpty()) {

                // TODO use companydetailservice to get company details

                Company c = new Company(null, companyName, null, null, null, null, null);
                log.info("Creating new company: {}", c);
                company = companyService.saveCompany(c);
            } else if (companies.size() == 1) {
                company = companies.getFirst();
            } else {
                log.error("Ambiguous company name: {}", companyName);
                return;
            }
        }

        if (company != null) {
            log.info("Company: {}", company);
            // TODO create job

            Job job= new Job(null, company.id(), jd.jobTitle(), page.url(), null, jd.minimumSalary(),
                    jd.maximumSalary(), "LinkedIn", null, jobDescriptionMarkdown);

            Job savedJob = jobService.saveJob(job);

            log.info("Saved job: {}", savedJob);

        } else {
            log.error("Failed to save company: {}", jd.companyName());
        }

    }
}
