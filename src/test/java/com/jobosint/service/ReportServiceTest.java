package com.jobosint.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReportServiceTest {
    @Autowired
    private ReportService reportService;

    @Test
    public void test() throws Exception {
        reportService.report();
    }
}
