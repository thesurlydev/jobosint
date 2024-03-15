package com.jobosint.parse;

import com.jobosint.model.JobDescription;
import com.jobosint.model.LinkedInJobDescription;
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

    public LinkedInJobDescription parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        Element body = doc.body();

        String title = body.select("h1").text();
        String company = body.select("body > div.application-outlet > div.authentication-outlet > div.scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow.job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) > div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div > a").text();

        ParseResult<JobDescription> parseResult = jobDescriptionParser.parse(body.toString(), "article");
        String rawMarkdown = parseResult.getData().getMarkdownBody();
        String jobDescriptionMarkdown = rawMarkdown.replace("{#job-details}", "");

        return new LinkedInJobDescription(title, company, jobDescriptionMarkdown);
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
