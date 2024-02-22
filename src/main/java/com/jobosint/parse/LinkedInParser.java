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

    public String parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document orig = Jsoup.parse(input, "UTF-8", "https://www.linkedin.com/");
        Element body = orig.body();
        body.children().comments().clear();
        body.select("a").clear();
        body.select("code").clear();
        body.select("iframe").clear();
        body.select("img").clear();
        body.select("meta").clear();
        body.select("script").clear();
        body.select("svg").clear();

        Elements tagLineElements = body.select("body > div.application-outlet > div.authentication-outlet > div" +
                ".scaffold-layout.scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow" +
                ".job-view-layout.jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) >" +
                " div > div > div.p5 > div.job-details-jobs-unified-top-card__primary-description-container > div");

        String tagLine
                = tagLineElements.text();
        String topCard = body.select("body > div.application-outlet > div.authentication-outlet > div.scaffold-layout" +
                ".scaffold-layout--breakpoint-xl.scaffold-layout--main-aside.scaffold-layout--reflow.job-view-layout" +
                ".jobs-details > div > div > main > div > div:nth-child(1) > div > div:nth-child(1) > div > div > div" +
                ".p5 > div.mt3.mb2").text();
        Elements detailsEl = body.select("#job-details");
        String details = detailsEl.text();

        return tagLine + "\n\n" + topCard + "\n\n" + details;
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
