package com.jobosint.parser;

import com.jobosint.convert.HtmlToMarkdownConverter;
import com.jobosint.parse.BuiltinParser;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("parse")
public class BuiltinParserTest {

    private final BuiltinParser parser = new BuiltinParser(new JobDescriptionParser(new HtmlToMarkdownConverter()));

    @Test
    public void testParse() throws IOException {
        parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240321-0700/2f13e03b-0c5d-4fe9-9027-f5909821a869.html");
    }
}
