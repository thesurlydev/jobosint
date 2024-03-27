package com.jobosint.parser;

import com.jobosint.model.CompanyParserResult;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("parse")
public class LinkedInParserTest {

    private final LinkedInParser parser = new LinkedInParser(new JobDescriptionParser());

    @Test
    public void parseJobDescription() throws Exception {
        JobDescriptionParserResult jobDescriptionParserResult = parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240219-0800/https-www" +
                "-linkedin-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html");
        assertNotNull(jobDescriptionParserResult);
        assertNotNull(jobDescriptionParserResult.description());
        assertEquals("Enterprise Architect", jobDescriptionParserResult.title());
        assertEquals("Slalom", jobDescriptionParserResult.companyName());
    }

    @Test
    public void parseSearchResults() throws Exception {
        parser.parseSearchResults("tester.html");
    }

    @Test void parseCompany() throws Exception {
        CompanyParserResult companyParserResult = parser.parseCompanyDescription("/home/shane/projects/jobosint/data/pages/20240327-0700/0f59631f-ee0f-4a8b-b044-8a6084a0105d.html");
        System.out.println(companyParserResult);
    }
}
