package com.jobosint.client;

import org.junit.jupiter.api.Test;

public class SimpleHttpClientTest {

    @Test
    public void test() throws Exception {
        SimpleHttpClient client = new SimpleHttpClient();
        client.testVoyager();
    }
}
