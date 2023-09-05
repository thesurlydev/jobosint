package com.jobosint.parser;

import org.junit.jupiter.api.Test;

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
