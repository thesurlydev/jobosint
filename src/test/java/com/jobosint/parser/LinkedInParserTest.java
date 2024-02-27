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
//        parser.parseJobDescription("/home/shane/projects/jobosint/src/test/resources/test1.html");
//        parser.parseJobDescription("/home/shane/projects/jobosint/src/test/resources/test2.html");
        String out = parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240219-0800/https-www" +
                "-linkedin-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html");
        System.out.println(out);
    }

    @Test
    public void parseSearchResults() throws Exception {
        LinkedInParser parser = new LinkedInParser();
        parser.parseSearchResults("tester.html");
    }

}
