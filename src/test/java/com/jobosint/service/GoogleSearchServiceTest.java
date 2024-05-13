package com.jobosint.service;

import com.jobosint.model.FetchAttribute;
import com.jobosint.model.GoogleSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GoogleSearchServiceTest {

    @Autowired GoogleSearchService googleSearchService;
    @Autowired ScrapeService scrapeService;

    @Test
    public void testSearch() {
        var request = new GoogleSearchRequest("java", 5);
        var response = googleSearchService.search(request);
        assertNotNull(response);
        assertEquals(5, response.results().size());
        response.results().stream()
                .map(result -> scrapeService.scrape(result.url(), FetchAttribute.text))
                .forEach(System.out::println);
    }
}
