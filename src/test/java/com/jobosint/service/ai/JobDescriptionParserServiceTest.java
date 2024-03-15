package com.jobosint.service.ai;

import com.jobosint.model.ai.JobDescriptionParseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class JobDescriptionParserServiceTest {

    @Autowired private JobDescriptionParserService jobDescriptionParserService;

    @Test
    public void test() {
        Optional<JobDescriptionParseResult> maybeJobDescriptionParseResult = jobDescriptionParserService.parseJobDescription("/home/shane/projects/jobosint/data/pages/20240219-0800/https-www-linkedin-com-jobs-view-3818479939-alternatechannel-search-refid-gpajzx5osaaopfjrwljxww-3d-3d-trackingid-hz50rlfh7ues4-2f0m-2b7nbtw-3d-3d.html");
        assert maybeJobDescriptionParseResult.isPresent();
        System.out.println(maybeJobDescriptionParseResult.get());
    }
}
