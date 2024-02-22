package com.jobosint.parse;

import com.jobosint.model.Company;
import com.jobosint.model.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class LinkedInParser {

    public void parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        String scriptsRemoved = doc.select("script").remove().toString();
        String metaRemoved = doc.select("meta").remove().toString();
//        System.out.println(scriptsRemoved);

        var title = doc.select("h1").text();

        Elements tagLineElements = doc.select("body > div.application-outlet > div.authentication-outlet > div" +
                ".scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow" +
                ".job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) >" +
                " div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div");

        String companyName = null;
        String companyUrl = null;
        for(Element element : tagLineElements.getFirst().children()) {
            if (element.tag().getName().equals("a")) {
                companyUrl = element.attr("href");
                companyName = element.text();
            }
            if (element.text().contains("applicants")) {
                System.out.println("applicants: " + element.text().replaceFirst(" applicants", ""));
            }
        }

//        System.out.println("tagline: " + tagLineElements);
//
//        System.out.println("company: " + doc.select("body > div.application-outlet > div.authentication-outlet > div" +
//                ".scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow" +
//                ".job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) >" +
//                " div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div > a").text());
//        System.out.println("applicants: " + doc.select("body > div.application-outlet > div.authentication-outlet > div.scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow.job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) > div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div > span.tvm__text.tvm__text--positive > strong").text());
//        System.out.println("description: " + doc.select("#job-details"));

        var company = new Company(null, companyName, companyUrl);
        System.out.println(company);

        System.out.println("---");
        var job = new Job(null, null, title, null, null, "ingested", null, null, null, null, null, null, null);
        System.out.println(job);
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
