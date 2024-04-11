package com.jobosint.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.workday.WorkdayLinkedData;
import com.jobosint.util.ConversionUtils;
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
public class WorkdayParser {

    private final ObjectMapper objectMapper;

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

        WorkdayLinkedData workdayLinkedData = objectMapper.readValue(linkedDataJson, WorkdayLinkedData.class);

        String descriptionSelector = "div[data-automation-id='jobPostingDescription']";
        Element descEl = doc.selectFirst(descriptionSelector);

        String description;
        if (descEl != null) {
            String descHtml = descEl.html();
            description = ConversionUtils.convertToMarkdown(descHtml);
        } else {
            description = ConversionUtils.convertToMarkdown(workdayLinkedData.getDescription());
        }

        String[] salaryRange = ParseUtils.parseSalaryRange(description);

        return new JobDescriptionParserResult(
                workdayLinkedData.getTitle(),
                workdayLinkedData.getHiringOrganization().getName(),
                description,
                salaryRange);
    }
}
