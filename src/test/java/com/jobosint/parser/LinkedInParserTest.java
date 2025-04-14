package com.jobosint.parser;

import com.jobosint.config.ScrapeConfig;
import com.jobosint.model.CompanyParserResult;
import com.jobosint.model.JobDescriptionParserResult;
import com.jobosint.model.ProfileParserResult;
import com.jobosint.parse.JobDescriptionParser;
import com.jobosint.parse.LinkedInParser;
import com.jobosint.util.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("parse")
public class LinkedInParserTest {

    private final LinkedInParser parser = new LinkedInParser(null, new JobDescriptionParser());


    @Test
    public void parseJobDescriptionFromContent() throws Exception {
        String content = FileUtils.readFromFile(Path.of("downloads/jobosint/linkedin-job-4209400522/index-good.html"));
        JobDescriptionParserResult jobDescriptionParserResult = parser.parseJobDescriptionFromContent(content);
        System.out.println(jobDescriptionParserResult);
    }

    @Test
    public void parseJobDescription() throws Exception {
        JobDescriptionParserResult jobDescriptionParserResult = parser.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240219-0800/https-www" +
                "-linkedin-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html");
        assertNotNull(jobDescriptionParserResult);
        assertNotNull(jobDescriptionParserResult.description());
        assertEquals("Enterprise Architect", jobDescriptionParserResult.title());
        assertEquals("Slalom", jobDescriptionParserResult.companyName());
        assertEquals("slalom-consulting", jobDescriptionParserResult.companySlug());
    }

    @Test void parseCompany() throws Exception {
        CompanyParserResult companyParserResult = parser.parseCompanyDescription("/home/shane/projects/jobosint/data/pages/20240327-0700/0f59631f-ee0f-4a8b-b044-8a6084a0105d.html");
        assertNotNull(companyParserResult);
    }

    @Test
    public void parseProfile() throws Exception {
        ProfileParserResult profileParserResult = parser.parseProfile("/home/shane/projects/jobosint/data/pages/20240408-0700/38e4bd65-31fa-48df-95ad-594794f0bd46.html", "https://www.linkedin.com/in/paul-shevsky-03251b134/");
        assertNotNull(profileParserResult);
    }
}
