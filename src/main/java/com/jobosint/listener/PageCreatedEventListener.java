package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.*;
import com.jobosint.integration.greenhouse.model.GetJobResult;
import com.jobosint.parse.*;
import com.jobosint.service.CompanyService;
import com.jobosint.service.ContactService;
import com.jobosint.integration.greenhouse.service.GreenhouseService;
import com.jobosint.service.JobService;
import com.jobosint.service.LinkedInService;
import com.jobosint.util.ConversionUtils;
import com.jobosint.util.ParseUtils;
import com.jobosint.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.data.util.Pair;
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
    private final LinkedInService linkedInService;

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
        String greenhouseToken = null;
        String linkedInToken = null;
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
                if (jobDescriptionParserResult.companySlug() != null) {
                    Company company = linkedInService.scrapeCompany(jobDescriptionParserResult.companySlug());
                    companyService.saveOrMergeCompany(company.name(), company);
                }
            } else if (url.startsWith("https://www.linkedin.com/company/")) {

                linkedInToken = linkedInService.getCompanyTokenFromUrl(page.url());
                CompanyParserResult companyParserResult = linkedInParser.parseCompanyDescription(contentPath);
                Company company = new Company(null,
                        companyParserResult.name(),
                        companyParserResult.websiteUrl(),
                        null,
                        companyParserResult.employeeCount(),
                        companyParserResult.summary(),
                        companyParserResult.location(),
                        linkedInToken,
                        greenhouseToken);
                companyService.saveOrMergeCompany(companyParserResult.name(), company);

            } else if (url.startsWith("https://www.linkedin.com/in/")) {

                ProfileParserResult profileParserResult = linkedInParser.parseProfile(contentPath, url);
                List<Company> companies = companyService.search("N/A");
                Company company = companies.getFirst();
                Contact contact = new Contact(null, company.id(), profileParserResult.fullName(),
                        profileParserResult.linkedInProfileUrl(), profileParserResult.title(),
                        null, null, null);
                Contact savedContact = contactService.saveContact(contact);
                log.info("Saved contact: {}", savedContact);

            } else if (url.contains(".myworkdayjobs.com/")) {
                jobDescriptionParserResult = workdayParser.parseJobDescription(contentPath);
                jobSource = "Workday";
            } else if (url.startsWith("https://jobs.lever.co/")) {
                jobDescriptionParserResult = leverParser.parseJobDescription(contentPath);
                jobSource = "Lever";
            } else if (url.startsWith("https://boards.greenhouse.io/")) {

                Pair<String, String> boardAndId = greenhouseService.getBoardTokenAndIdFromUrl(page.url());

                greenhouseToken = boardAndId.getFirst();
                String jobId = boardAndId.getSecond();

                // instead of parsing the page content, we can use the greenhouse API to get the job details
                GetJobResult result = greenhouseService.getJobResult(greenhouseToken, jobId);

                var escapedContent = StringEscapeUtils.unescapeHtml4(result.job().content());

                // we need to convert the content to markdown
                String markdownContent = ConversionUtils.convertToMarkdown(escapedContent);

                SalaryRange salaryRange = ParseUtils.parseSalaryRange(markdownContent);

                jobDescriptionParserResult = new JobDescriptionParserResult(result.job().title(), result.boardToken(),result.boardToken(), markdownContent, salaryRange);
                jobSource = "Greenhouse";

            } else {
                log.error("Unsupported url: {}", url);
                return;
            }

            if (jobDescriptionParserResult != null) {
                Company company = companyService.saveOrMergeCompany(jobDescriptionParserResult.companyName(), null);
                if (company != null) {
                    processJob(jobDescriptionParserResult, page, company, jobSource);
                }
            }

        } catch (IOException e) {
            log.error("Failed to parse job description: {}", contentPath, e);
        }
    }

    private void processJob(JobDescriptionParserResult jobDescriptionParserResult,
                            Page page,
                            Company company,
                            String jobSource) {


        String url = StringUtils.removeQueryString(page.url());
        String jobDescription = jobDescriptionParserResult.description();

        Job job = new Job(null,
                company.id(),
                null,
                jobDescriptionParserResult.title(),
                url,
                jobDescriptionParserResult.salaryRange().min(),
                jobDescriptionParserResult.salaryRange().max(),
                jobSource,
                null,
                jobDescription,
                "Active",
                page.id()
        );

        jobService.saveJob(job);
    }
}
