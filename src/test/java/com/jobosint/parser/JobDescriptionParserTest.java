package com.jobosint.parser;

import com.jobosint.convert.HtmlToMarkdownConverter;
import com.jobosint.model.JobDescription;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.ParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("parse")
public class JobDescriptionParserTest {

    private JobDescriptionParser jobDescriptionParser;

    @BeforeEach
    public void setup() {
        jobDescriptionParser = new JobDescriptionParser(new HtmlToMarkdownConverter());
    }

    @Test
    public void parse() throws IOException {
        String path = "/home/shane/projects/jobosint/data/pages/20240219-0800/https-www-linkedin-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html";
        ParseResult<JobDescription> result = jobDescriptionParser.parse(path, "article");
        assertNotNull(result);
    }
}
