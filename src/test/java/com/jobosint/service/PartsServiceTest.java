package com.jobosint.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PartsServiceTest {

    @Autowired private PartService partService;

    @Test
    public void refresh() throws Exception {
        partService.refresh(true);
        Thread.sleep(5000);
    }
}
