package com.jobosint.service.ai;

import com.jobosint.model.ai.CompanyDetail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class CompanyDetailsServiceTest {

    @Autowired private CompanyDetailsService companyDetailsService;

    @Test
    public void test() {
        CompanyDetail detail = companyDetailsService.getCompanyDetails("gitlab");
        System.out.println(detail);
    }
}
