package com.jobosint.parser;

import com.jobosint.parse.LinkedInParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("parse")
@Disabled
public class LinkedInParserTest {

    @Test
    public void parseJobDescription() throws Exception {
        LinkedInParser parser = new LinkedInParser();
        parser.parseJobDescription("/home/shane/projects/jobosint/src/test/resources/test1.html");
        parser.parseJobDescription("/home/shane/projects/jobosint/src/test/resources/test2.html");
    }

    @Test
    public void parseSearchResults() throws Exception {
        LinkedInParser parser = new LinkedInParser();
        parser.parseSearchResults("tester.html");
    }

}
