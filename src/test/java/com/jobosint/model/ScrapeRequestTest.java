package com.jobosint.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScrapeRequestTest {

    @Test
    public void host() {
        var request = new ScrapeRequest("https://www.surly.dev");
        assertEquals("https://www.surly.dev", request.url());
    }
}
