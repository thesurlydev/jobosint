package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.Page;
import com.jobosint.model.ai.JobDescriptionParseResult;
import com.jobosint.service.CompanyService;
import com.jobosint.service.JobService;
import com.jobosint.service.PageService;
import com.jobosint.service.ai.JobDescriptionParserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final PageService pageService;
    private final JobService jobService;
    private final JobDescriptionParserService jobDescriptionParserService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        // TODO
        Page page = event.getPage();
        String contentPath = page.contentPath();

        Optional<JobDescriptionParseResult> jobDescriptionParseResult = jobDescriptionParserService.parseJobDescription(contentPath);

        if (jobDescriptionParseResult.isEmpty()) {
            log.error("Failed to parse job description: {}", contentPath);
            return;
        }
        JobDescriptionParseResult jd = jobDescriptionParseResult.get();

        log.info("Parsed job description: {}", jd);

        List<Company> companies = companyService.search(jd.companyName());
        Company company;
        if (companies.isEmpty()) {
            Company c = new Company(null, jd.companyName(), null, null, null, null, null);
            log.info("Creating new company: {}", c);
            company = companyService.saveCompany(c);
        } else if (companies.size() == 1) {
            company = companies.getFirst();
        } else {
            log.error("Ambiguous company name: {}", jd.companyName());
            return;
        }

        if (company != null) {
            log.info("Company: {}", company);
            // TODO create job

            Job job= new Job(null, company.id(), jd.jobTitle(), page.url(), null, jd.minimumSalary(),
                    jd.maximumSalary(), "LinkedIn", null);

            Job savedJob = jobService.saveJob(job);

            log.info("Saved job: {}", savedJob);

        } else {
            log.error("Failed to save company: {}", jd.companyName());
        }

    }
}
