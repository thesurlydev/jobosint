package com.jobosint.parser;

import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.parse.BuiltinParser;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.SmartRecruiterParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("parse")
public class SmartRecruiterParserTest {

    private final SmartRecruiterParser parser = new SmartRecruiterParser(new JobDescriptionParser());

    @Test
    public void testParse() throws IOException {
        JobDescriptionParserResult result = parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240329-0700/ac2116d9-9b26-4ec2-91f4-ac94c08548b0.html");
        assertEquals("Staff Software Engineer - Identity and Access Management, Trust", result.title());
    }
}
