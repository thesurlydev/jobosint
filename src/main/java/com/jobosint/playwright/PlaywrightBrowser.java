package com.jobosint.playwright;

import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import jakarta.annotation.PreDestroy;

@Configuration
@Slf4j
public class PlaywrightBrowser {

    private Playwright playwright;

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        playwright = Playwright.create();
        
        stopWatch.stop();
        log.info("Playwright instance created in {} ms", stopWatch.getTotalTimeMillis());
        return playwright;
    }
    
    @PreDestroy
    public void cleanUp() {
        log.info("Cleaning up Playwright resources");
        if (playwright != null) {
            try {
                playwright.close();
                log.info("Closed Playwright instance");
            } catch (Exception e) {
                log.error("Error closing Playwright instance", e);
            }
        }
    }
}
