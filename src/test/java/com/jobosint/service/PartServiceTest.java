package com.jobosint.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PartServiceTest {

    @Autowired private PartService partService;

    @Test
    public void refresh() throws Exception {
        partService.refresh(true, false);
        Thread.sleep(3000);
    }

}
