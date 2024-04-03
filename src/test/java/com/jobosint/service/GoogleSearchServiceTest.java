package com.jobosint.service;

import com.jobosint.model.GoogleSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GoogleSearchServiceTest {

    @Autowired GoogleSearchService googleSearchService;

    @Test
    public void testSearch() {
        var request = new GoogleSearchRequest("java", 10);
        var response = googleSearchService.search(request);
        assertNotNull(response);
        assertEquals(10, response.results().size());
    }
}
