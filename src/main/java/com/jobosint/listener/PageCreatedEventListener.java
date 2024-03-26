package com.jobosint.listener;

import com.jobosint.convert.HtmlToMarkdownConverter;
import com.jobosint.event.PageCreatedEvent;
import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.Page;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.model.ai.JobAttributes;
import com.jobosint.model.greenhouse.GetJobResult;
import com.jobosint.parse.BuiltinParser;
import com.jobosint.parse.LeverParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.parse.WorkdayParser;
import com.jobosint.service.CompanyService;
import com.jobosint.service.GreenhouseService;
import com.jobosint.service.JobService;
import com.jobosint.service.ai.CompanyDetailsService;
import com.jobosint.service.ai.JobAttributeService;
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
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {

    private final CompanyService companyService;
    private final CompanyDetailsService companyDetailsService;
    private final GreenhouseService greenhouseService;
    private final JobService jobService;
    private final LinkedInParser linkedInParser;
    private final BuiltinParser builtInParser;
    private final WorkdayParser workdayParser;
    private final LeverParser leverParser;
    private final JobAttributeService jobAttributesService;

    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {

        log.info("Received: {}", event);

        Page page = event.getPage();
        String contentPath = page.contentPath();
        String jobSource;

        JobDescriptionParserResult jobDescriptionParserResult = null;
        try {

            var url = page.url();
            if (url.startsWith("https://builtin.com/job/")) {
                jobDescriptionParserResult = builtInParser.parseJobDescription(contentPath);
                jobSource = "Builtin";
            } else if (url.startsWith("https://www.linkedin.com/jobs/view/")) {
                jobDescriptionParserResult = linkedInParser.parseJobDescription(contentPath);
                jobSource = "LinkedIn";
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
                HtmlToMarkdownConverter converter = new HtmlToMarkdownConverter();
                String markdownContent = converter.convertToMarkdown(escapedContent);

                String[] salaryRange = ParseUtils.parseSalaryRange(markdownContent);

                jobDescriptionParserResult = new JobDescriptionParserResult(result.job().title(), result.boardToken(), markdownContent, salaryRange);
                jobSource = "Greenhouse";
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
                Company c = new Company(null, companyName, detail.websiteLink(), detail.stockTicker(),
                        detail.numberOfEmployees(), detail.summary(), detail.location());
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
        Optional<JobAttributes> jobAttributes = jobAttributesService.parseJobDescription(jobDescription);
        Job job;
        if (jobAttributes.isPresent()) {
            JobAttributes attributes = jobAttributes.get();
            List<String> interviewSteps = attributes.interviewProcess().processSteps();
            List<String> programmingLanguages = attributes.technologyStack().programmingLanguages();
            List<String> databases = attributes.technologyStack().databases();
            List<String> frameworks = attributes.technologyStack().frameworks();
            List<String> cloudServices = attributes.technologyStack().cloudServices();
            List<String> cloudProviders = attributes.technologyStack().cloudProviders();
            List<String> requiredQualifications = attributes.jobQualifications().required();
            List<String> preferredQualifications = attributes.jobQualifications().preferred();
            List<String> cultureValues = attributes.culture().values();

            job = new Job(null,
                    company.id(),
                    jobDescriptionParserResult.title(),
                    url,
                    salaryMin,
                    salaryMax,
                    jobSource,
                    null,
                    jobDescription,
                    "Active",
                    page.id(),
                    interviewSteps,
                    programmingLanguages,
                    databases,
                    frameworks,
                    cloudServices,
                    cloudProviders,
                    requiredQualifications,
                    preferredQualifications,
                    cultureValues
            );

        } else {
            log.error("Failed to parse job attributes");
            job = new Job(null,
                    company.id(),
                    jobDescriptionParserResult.title(),
                    url,
                    salaryMin,
                    salaryMax,
                    jobSource,
                    null,
                    jobDescription,
                    "Active",
                    page.id(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return jobService.saveJob(job);
    }
}
