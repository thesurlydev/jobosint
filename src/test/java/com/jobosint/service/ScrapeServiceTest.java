package com.jobosint.service;

import com.jobosint.model.FetchAttribute;
import com.jobosint.model.ScrapeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ScrapeServiceTest {

    @Autowired
    ScrapeService scrapeService;

    @Test
    public void testScrape() {
        var response = scrapeService.scrape("https://www.surly.dev", FetchAttribute.text);
        assertNotNull(response);
        assertEquals(0, response.errors().size());
        assertEquals(1, response.data().size());
        assertEquals("https://www.surly.dev", response.base_url());
    }
}
