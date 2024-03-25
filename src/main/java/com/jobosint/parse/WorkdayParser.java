package com.jobosint.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.workday.WorkdayLinkedData;
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
    private final JobDescriptionParser jobDescriptionParser;

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
        WorkdayLinkedData workdayLinkedData = mapper.readValue(linkedDataJson, WorkdayLinkedData.class);

        return new JobDescriptionParserResult(
                workdayLinkedData.getTitle(),
                workdayLinkedData.getHiringOrganization().getName(),
                workdayLinkedData.getDescription());
    }
}
