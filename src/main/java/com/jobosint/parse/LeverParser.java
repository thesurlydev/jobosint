package com.jobosint.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.convert.HtmlToMarkdownConverter;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.lever.LeverLinkedData;
import com.jobosint.util.ParseUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class LeverParser {
    private final HtmlToMarkdownConverter htmlToMarkdownConverter;

    public JobDescriptionParserResult parseJobDescription(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");

        Element linkedDataScript = doc.selectFirst("script[type='application/ld+json']");
        String linkedDataJson;
        if (linkedDataScript == null) {
            throw new IllegalArgumentException("No linked data script found in: ${path}");
        } else {
            linkedDataJson = linkedDataScript.data();
        }

        ObjectMapper mapper = new ObjectMapper();
        LeverLinkedData leverLinkedData = mapper.readValue(linkedDataJson, LeverLinkedData.class);

        String markdown = htmlToMarkdownConverter.convertToMarkdown(leverLinkedData.getDescription());

        String[] salaryRange = ParseUtils.parseSalaryRange(markdown);

        return new JobDescriptionParserResult(
                leverLinkedData.getTitle(),
                leverLinkedData.getHiringOrganization().getName(),
                markdown,
                salaryRange);
    }
}