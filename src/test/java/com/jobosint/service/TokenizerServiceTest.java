package com.jobosint.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerServiceTest {
    TokenizerService tokenizerService = new TokenizerService();

    @Test
    public void testCountTokens() {
        Integer result = tokenizerService.countTokens("text");
        assertEquals(1, result);

        Integer result2 = tokenizerService.countTokens("hello world foo bar");
        assertEquals(4, result2);
    }
}
