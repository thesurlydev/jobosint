package com.jobosint.parse;


import com.jobosint.model.JobDescription;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.SalaryRange;
import com.jobosint.util.ParseUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/*
1 = {Element@3685} "<meta name="viewport" content="width=device-width, initial-scale=1.0">"
2 = {Element@3686} "<meta name="description" content="Cash App has grown rapidly to 70+ million users moving billions of dollars daily. To support this, we are building out Trust — a new Platform organization responsible for product experiences aimed at promoting the health of the increasingly complex Cash App ecosystem. Trust works to improve Cash App...">"
3 = {Element@3687} "<meta name="keywords" content="Block Staff Software Engineer - Identity and Access Management, Trust San Francisco, CA, United States jobs careers">"
4 = {Element@3688} "<meta name="sr:job-ad-id" content="d79edda6-31c3-4c3b-a66d-a0cc40b1a3dc">"
5 = {Element@3689} "<meta property="og:title" content="Staff Software Engineer - Identity and Access Management, Trust">"
6 = {Element@3690} "<meta property="og:type" content="object">"
7 = {Element@3691} "<meta property="og:url" content="https://jobs.smartrecruiters.com/Square/743999977220153-staff-software-engineer-identity-and-access-management-trust">"
8 = {Element@3692} "<meta property="og:image" content="https://c.smartrecruiters.com/sr-company-images-prod-aws-dc5/56131d6ae4b0ea6ac239d855/4e576205-3361-4054-af60-0c77dd8014de_social_logo/300x300?r=s3-eu-central-1&amp;_1663861738561">"
9 = {Element@3693} "<meta property="og:description" content="Company Description: It all started with an idea at Block in 2013. Initially built to take the pain out of peer-to-peer payments, Cash App has gone from a simple product with a single purpose to a dynamic ecosystem, developing unique financial products, including Afterpay/Clearpay, to provide a better way to send, spend, invest, borrow and save to our 47 million monthly active customers. We want to redefine the world’s relationship with money to make it more relatable, instantly available, and universally accessible.\n\n\n\nToday, Cash App has thousands of employees working globally across office and remote locations, with a culture geared toward innovation, collaboration and impact. We’ve been a distributed team since day one, and many of our roles can be done remotely from the countries where Cash App operates. No matter the location, we tailor our experience to ensure our employees are creative, productive, and happy.\n\n\n\nCheck out our locations, "
10 = {Element@3694} "<meta property="og:site_name" content="Block">"
11 = {Element@3695} "<meta itemprop="og:updated_time" content="2024-03-29T18:12:42.537Z">"
12 = {Element@3696} "<meta property="fb:app_id" content="130516673696773">"
13 = {Element@3697} "<meta name="twitter:card" content="summary_large_image">"
14 = {Element@3698} "<meta name="twitter:site" content="@SmartRecruiters">"
15 = {Element@3699} "<meta name="twitter:creator" content="@SmartRecruiters">"
16 = {Element@3700} "<meta name="twitter:title" content="Block is looking for a Staff Software Engineer - Identity and Access Management, Trust in San Francisco, CA, United States">"
17 = {Element@3701} "<meta name="twitter:description" content="Cash App has grown rapidly to 70+ million users moving billions of dollars daily. To support this, we are building out Trust — a new Platform organization responsible for product experiences aimed...">"
18 = {Element@3702} "<meta name="twitter:image" content="https://c.smartrecruiters.com/sr-company-images-prod-aws-dc5/56131d6ae4b0ea6ac239d855/4e576205-3361-4054-af60-0c77dd8014de_social_logo/300x300?r=s3-eu-central-1&amp;_1663861738561">"
19 = {Element@3703} "<meta name="twitter:label1" value="Location">"
20 = {Element@3704} "<meta name="twitter:data1" value="San Francisco, CA, United States">"
21 = {Element@3705} "<meta name="twitter:label2" value="Posted">"
22 = {Element@3706} "<meta name="twitter:data2" value="Mar 29, 2024">"
23 = {Element@3707} "<meta itemprop="addressCountry" content="United States">"
24 = {Element@3708} "<meta itemprop="addressLocality" content="San Francisco">"
25 = {Element@3709} "<meta itemprop="addressRegion" content="California">"
26 = {Element@3710} "<meta itemprop="hiringOrganization" content="Block">"
27 = {Element@3711} "<meta itemprop="datePosted" content="2024-03-29T18:12:42.537Z">"
28 = {Element@3712} "<meta itemprop="industry" content="Information Technology And Services">"
 */


@RequiredArgsConstructor
@Component
public class SmartRecruiterParser {
    private final JobDescriptionParser jobDescriptionParser;

    public JobDescriptionParserResult parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://jobs.smartrecruiters.com/");
        Element body = doc.body();

        String title = getOgMetaContent(doc, "og:title");
        String company = getOgMetaContent(doc, "og:site_name");

        ParseResult<JobDescription> parseResult = jobDescriptionParser.parse(body.toString(), "div.job-sections");
        String rawMarkdown = parseResult.getData().getMarkdownBody();

        SalaryRange salaryRange = ParseUtils.parseSalaryRange(rawMarkdown);

        return new JobDescriptionParserResult(title, company, rawMarkdown, salaryRange);
    }

    private String getOgMetaContent(Document doc, String property) {
        return doc.select("meta[property='" + property + "']").attr("content");
    }
}
