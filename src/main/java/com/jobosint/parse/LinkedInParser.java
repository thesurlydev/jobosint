package com.jobosint.parse;

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
        System.out.println();
        System.out.println("name: " + doc.select("h1").text());

        System.out.println("company: " + doc.select("body > div.application-outlet > div.authentication-outlet > div > div.job-view-layout.jobs-details > div.grid > div > div:nth-child(1) > div > div > div.p5 > div.jobs-unified-top-card__primary-description > div > a"));
        System.out.println("applicants: " + doc.select("body > div.application-outlet > div.authentication-outlet > div > div.job-view-layout.jobs-details > div.grid > div > div:nth-child(1) > div > div > div.p5 > div.jobs-unified-top-card__primary-description > div > span:nth-child(6)").text());
        System.out.println("description: " + doc.select("#job-details"));

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
