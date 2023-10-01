package com.jobosint.client;

import org.springframework.stereotype.Component;

import java.net.CookieManager;
import java.net.http.HttpClient;

@Component
public class HttpClientFactory {
    public HttpClient getClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
}
