package com.jobosint.parse;

import com.jobosint.model.*;
import com.jobosint.util.ParseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class LinkedInParser {

    private final JobDescriptionParser jobDescriptionParser;

    public ProfileParserResult parseProfile(String path, String linkedInProfileUrl) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        Element body = doc.body();

        String fullName = body.select("h1").text();
        String title = body.select("div.text-body-medium").text();

        return new ProfileParserResult(fullName, title, linkedInProfileUrl);
    }

    public CompanyParserResult parseCompanyDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        return parseCompanyDescription(doc);
    }

    public CompanyParserResult parseCompanyDescriptionFromString(String content) {
        Document doc = Jsoup.parse(content);
        return parseCompanyDescription(doc);
    }

    private @NotNull CompanyParserResult parseCompanyDescription(Document doc) {
        Element body = doc.body();

        String title = body.select("h1").text();

        if (title.equals("Sign in")) {
            throw new RuntimeException("Encountered Sign in page; update cookies!");
        }

        Element mainSection = body.select("section").get(2);

        String summary = "n/a";
        Optional<Element> maybeSummaryEl =  Optional.ofNullable(mainSection.selectFirst("p"));
        if (maybeSummaryEl.isPresent()) {
            summary = maybeSummaryEl.get().text();
        }

        String employeeCountRaw = parseCompanySection(mainSection, "Company size");
        String employeeCount = employeeCountRaw.split(" ")[0];

        String websiteUrl = parseCompanySection(mainSection, "Website");
        String industry = parseCompanySection(mainSection, "Industry");
        String location = parseCompanySection(mainSection, "Headquarters");

        return new CompanyParserResult(title, websiteUrl, summary, employeeCount, industry, location);
    }

    private String parseCompanySection(Element mainSectionEl, String dtName) {
        String result = "n/a";
        try {
            Optional<Element> maybe = Optional.ofNullable(mainSectionEl.selectFirst("dt:contains(" + dtName + ")"));
            if (maybe.isPresent()) {
                Optional<Element> maybeSibling = Optional.ofNullable(maybe.get().nextElementSibling());
                if (maybeSibling.isPresent()) {
                    result = maybeSibling.get().text();
                }
            }
        } catch (Exception e) {
            log.warn("Unable to parse {}: {}", dtName, e.getMessage());
        }
        return result;
    }

    public JobDescriptionParserResult parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        Element body = doc.body();

        String title = body.select("h1").text();
        String company = body.select("body > div.application-outlet > div.authentication-outlet > div.scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow.job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) > div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div > a").text();

        ParseResult<JobDescription> parseResult = jobDescriptionParser.parse(body.toString(), "article");
        String rawMarkdown = parseResult.getData().getMarkdownBody();
        String jobDescriptionMarkdown = rawMarkdown.replace("{#job-details}", "");

        SalaryRange salaryRange = ParseUtils.parseSalaryRange(jobDescriptionMarkdown);

        return new JobDescriptionParserResult(title, company, jobDescriptionMarkdown, salaryRange);
    }

    public void parseSearchResults(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");

        Elements elements = doc.select("li.jobs-search-results__list-item");
        for (Element element : elements) {
            String id = element.attr("data-occludable-job-id");
            System.out.println(id);
        }
    }
}
