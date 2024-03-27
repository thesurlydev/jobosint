package com.jobosint.parse;

import com.jobosint.model.CompanyParserResult;
import com.jobosint.model.JobDescription;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.util.ParseUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class LinkedInParser {

    private final JobDescriptionParser jobDescriptionParser;

    public CompanyParserResult parseCompanyDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        Element body = doc.body();

        String title = body.select("h1").text();
        Element mainSection = body.select("section").get(2);
        String summary = mainSection.selectFirst("p").text();
        String employeeCountRaw = mainSection.selectFirst("dt:contains(Company size)").nextElementSibling().text();
        String employeeCount = employeeCountRaw.split(" ")[0];

        String websiteUrl = mainSection.selectFirst("dt:contains(Website)").nextElementSibling().text();
        String industry = mainSection.selectFirst("dt:contains(Industry)").nextElementSibling().text();
        String location = mainSection.selectFirst("dt:contains(Headquarters)").nextElementSibling().text();

        return new CompanyParserResult(title, websiteUrl, summary, employeeCount, industry, location);
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

        String[] salaryRange = ParseUtils.parseSalaryRange(jobDescriptionMarkdown);

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
