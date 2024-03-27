package com.jobosint.parser;

import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.parse.BuiltinParser;
import com.jobosint.parse.JobDescriptionParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("parse")
public class BuiltinParserTest {

    private final BuiltinParser parser = new BuiltinParser(new JobDescriptionParser());

    @Test
    public void testParse() throws IOException {
        JobDescriptionParserResult result = parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240321-0700/2f13e03b-0c5d-4fe9-9027-f5909821a869.html");
        assertEquals("Sr. Software Engineer (Patient Information Capture)", result.title());
    }
}
