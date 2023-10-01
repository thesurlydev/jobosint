package com.jobosint.parser;

import com.jobosint.model.JobDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class JobDescriptionParserTest {

    private JobDescriptionParser jobDescriptionParser;

    @BeforeEach
    public void setup() {
        jobDescriptionParser = new JobDescriptionParser();
    }

    @Test
    public void parse() throws IOException {
        String path = "/home/shane/projects/jobosint/tester.html";
        ParseResult<JobDescription> result = jobDescriptionParser.parse(path);
        System.out.println(result);
    }
}
