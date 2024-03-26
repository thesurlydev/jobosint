package com.jobosint.parse;

import com.jobosint.model.JobDescription;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.util.ParseUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BuiltinParser {
    private final JobDescriptionParser jobDescriptionParser;

    public JobDescriptionParserResult parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8", "https://builtin.com/");
        Element body = doc.body();

        String title = body.select("h1.node-title").text();
        String company = body.select("#page-main-content > div > div > div > div > div.l-content.right > div.row.row-region-top > div > div > div > div.block-content > div.company-title").text();

        ParseResult<JobDescription> parseResult = jobDescriptionParser.parse(body.toString(), "#page-main-content > div > div > div > div > div.l-content.right > div.row.row-region-middle > div > div > div.block.block-ctools.block-entity-viewnode > article > div.node__content > div.job-description");
        String rawMarkdown = parseResult.getData().getMarkdownBody();

        String[] salaryRange = ParseUtils.parseSalaryRange(rawMarkdown);

        return new JobDescriptionParserResult(title, company, rawMarkdown, salaryRange);
    }
}
