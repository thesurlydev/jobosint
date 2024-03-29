package com.jobosint.integration;

import com.jobosint.integration.pdl.CompanyEnrichResponse;
import com.jobosint.integration.pdl.PeopleDataLabsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PeopleDataLabsClientTest {

    @Autowired
    PeopleDataLabsClient client;

    @Test
    public void test() {
        Optional<CompanyEnrichResponse> maybeResponse = client.enrichCompany("stripe");
        assertTrue(maybeResponse.isPresent());
    }
}
