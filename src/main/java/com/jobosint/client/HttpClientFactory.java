package com.jobosint.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class HttpClientFactory {

    public HttpClient getClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
    
    /**
     * Gets an HTTP client with debug logging for requests and responses
     * @return HttpClient with logging enabled
     */
    public HttpClient getClientWithLogging() {
        return new LoggingHttpClient(getClient());
    }
    
    /**
     * Wrapper class that adds logging to an existing HttpClient
     */
    private static class LoggingHttpClient extends HttpClient {
        private final HttpClient delegate;
        
        public LoggingHttpClient(HttpClient delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) 
                throws IOException, InterruptedException {
            logRequest(request);
            HttpResponse<T> response = delegate.send(request, responseBodyHandler);
            logResponse(response);
            return response;
        }
        
        private void logRequest(HttpRequest request) {
            log.debug("HTTP Request: {} {}", request.method(), request.uri());
            request.headers().map().forEach((name, values) -> 
                log.debug("Request Header: {} = {}", name, String.join(", ", values)));
        }
        
        private <T> void logResponse(HttpResponse<T> response) {
            log.debug("HTTP Response: {} {}", response.statusCode(), response.uri());
            response.headers().map().forEach((name, values) -> 
                log.debug("Response Header: {} = {}", name, String.join(", ", values)));
            
            /*if (response.body() instanceof String) {
                log.debug("Response Body: {}", response.body());
            } else {
                log.debug("Response Body: [non-string body]");
            }*/
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return delegate.cookieHandler();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return delegate.connectTimeout();
        }

        @Override
        public Redirect followRedirects() {
            return delegate.followRedirects();
        }

        @Override
        public Optional<java.net.ProxySelector> proxy() {
            return delegate.proxy();
        }

        @Override
        public SSLContext sslContext() {
            return delegate.sslContext();
        }

        @Override
        public SSLParameters sslParameters() {
            return delegate.sslParameters();
        }

        @Override
        public Optional<java.net.Authenticator> authenticator() {
            return delegate.authenticator();
        }

        @Override
        public HttpClient.Version version() {
            return delegate.version();
        }

        @Override
        public Optional<java.util.concurrent.Executor> executor() {
            return delegate.executor();
        }

        @Override
        public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                              HttpResponse.BodyHandler<T> responseBodyHandler) {
            logRequest(request);
            return delegate.sendAsync(request, responseBodyHandler)
                    .thenApply(response -> {
                        logResponse(response);
                        return response;
                    });
        }

        @Override
        public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                              HttpResponse.BodyHandler<T> responseBodyHandler,
                                                              HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            logRequest(request);
            return delegate.sendAsync(request, responseBodyHandler, pushPromiseHandler)
                    .thenApply(response -> {
                        logResponse(response);
                        return response;
                    });
        }
    }
}
