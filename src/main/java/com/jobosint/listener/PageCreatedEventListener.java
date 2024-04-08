package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.*;
import com.jobosint.model.greenhouse.GetJobResult;
import com.jobosint.parse.*;
import com.jobosint.service.CompanyService;
import com.jobosint.service.ContactService;
import com.jobosint.service.GreenhouseService;
import com.jobosint.service.JobService;
import com.jobosint.service.ai.CompanyDetailsService;
import com.jobosint.util.ConversionUtils;
import com.jobosint.util.ParseUtils;
import com.jobosint.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final ContactService contactService;
    private final GreenhouseService greenhouseService;
    private final JobService jobService;

    private final LinkedInParser linkedInParser;
    private final BuiltinParser builtInParser;
    private final WorkdayParser workdayParser;
    private final LeverParser leverParser;
    private final SmartRecruiterParser smartRecruiterParser;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        Page page = event.getPage();
        String contentPath = page.contentPath();

        String jobSource = null;
        CompanyParserResult companyParserResult = null;
        ProfileParserResult profileParserResult = null;
        JobDescriptionParserResult jobDescriptionParserResult = null;

        try {

            var url = page.url();
            if (url.startsWith("https://jobs.smartrecruiters.com/")) {
                jobDescriptionParserResult = smartRecruiterParser.parseJobDescription(contentPath);
                jobSource = "SmartRecruiters";
            } else if (url.startsWith("https://builtin.com/job/")) {
                jobDescriptionParserResult = builtInParser.parseJobDescription(contentPath);
                jobSource = "Builtin";
            } else if (url.startsWith("https://www.linkedin.com/jobs/view/")) {
                jobDescriptionParserResult = linkedInParser.parseJobDescription(contentPath);
                jobSource = "LinkedIn";
            } else if (url.startsWith("https://www.linkedin.com/company/")) {

                companyParserResult = linkedInParser.parseCompanyDescription(contentPath);

            } else if (url.startsWith("https://www.linkedin.com/in/")) {

                profileParserResult = linkedInParser.parseProfile(contentPath, url);

            } else if (url.contains(".myworkdayjobs.com/")) {
                jobDescriptionParserResult = workdayParser.parseJobDescription(contentPath);
                jobSource = "Workday";
            } else if (url.startsWith("https://jobs.lever.co/")) {
                jobDescriptionParserResult = leverParser.parseJobDescription(contentPath);
                jobSource = "Lever";
            } else if (url.startsWith("https://boards.greenhouse.io/")) {

                // instead of parsing the page content, we can use the greenhouse API to get the job details
                GetJobResult result = greenhouseService.getJob(page.url());

                var escapedContent = StringEscapeUtils.unescapeHtml4(result.job().content());

                // we need to convert the content to markdown
                String markdownContent = ConversionUtils.convertToMarkdown(escapedContent);

                String[] salaryRange = ParseUtils.parseSalaryRange(markdownContent);

                jobDescriptionParserResult = new JobDescriptionParserResult(result.job().title(), result.boardToken(), markdownContent, salaryRange);
                jobSource = "Greenhouse";
            } else {
                log.error("Unsupported url: {}", url);
                return;
            }

            if (companyParserResult != null) {
                Company company = new Company(null,
                        companyParserResult.name(),
                        companyParserResult.websiteUrl(),
                        null,
                        companyParserResult.employeeCount(),
                        companyParserResult.summary(),
                        companyParserResult.location());
                processCompany(companyParserResult.name(), company);
            }

            if (profileParserResult != null) {
                List<Company> companies = companyService.search("N/A");
                Company company = companies.getFirst();
                Contact contact = new Contact(null, company.id(), profileParserResult.fullName(),
                        profileParserResult.linkedInProfileUrl(), profileParserResult.title(),
                        null, null, null);
                Contact savedContact = contactService.saveContact(contact);
                log.info("Saved contact: {}", savedContact);
            }

            if (jobDescriptionParserResult != null) {
                Company company = processCompany(jobDescriptionParserResult.companyName(), null);
                if (company != null) {
                    processJob(jobDescriptionParserResult, page, company, jobSource);
                }
            }
        } catch (IOException e) {
            log.error("Failed to parse job description: {}", contentPath, e);
        }
    }

    private Company processCompany(String companyName, Company companyCandidate) {
        Company company = null;
        if (companyName.isBlank()) {
            log.warn("Using N/A for company");
            List<Company> companies = companyService.search("N/A");
            company = companies.getFirst();
        } else {
            List<Company> companies = companyService.search(companyName);
            if (companies.isEmpty()) {
                Company c = companyCandidate;
                if (c == null) {
                    c = new Company(null, companyName, null, null,
                            null, null, null);
                }
                log.info("Creating new company: {}", c);
                company = companyService.saveCompany(c);
            } else if (companies.size() == 1) {
                if (companyCandidate != null) {
                    // merge
                    Company existingCompany = companies.getFirst();
                    company = companyService.mergeCompany(existingCompany, companyCandidate);
                } else {
                    company = companies.getFirst();
                }

            } else {
                log.error("Ambiguous company name: {}", companyName);
            }
        }
        return company;
    }

    private void processJob(JobDescriptionParserResult jobDescriptionParserResult,
                           Page page,
                           Company company,
                           String jobSource) {

        String salaryMin = null, salaryMax = null;
        if (jobDescriptionParserResult.salaryRange() != null) {
            if (jobDescriptionParserResult.salaryRange().length == 1) {
                salaryMin = jobDescriptionParserResult.salaryRange()[0];
            } else if (jobDescriptionParserResult.salaryRange().length == 2) {
                salaryMin = jobDescriptionParserResult.salaryRange()[0];
                salaryMax = jobDescriptionParserResult.salaryRange()[1];
            } else {
                log.warn("Unexpected salary range length: {}", jobDescriptionParserResult.salaryRange().length);
            }
        }

        String url = StringUtils.removeQueryString(page.url());
        String jobDescription = jobDescriptionParserResult.description();

        Job job = new Job(null,
                company.id(),
                jobDescriptionParserResult.title(),
                url,
                salaryMin,
                salaryMax,
                jobSource,
                null,
                jobDescription,
                "Active",
                page.id()
        );

        jobService.saveJob(job);
    }
}
