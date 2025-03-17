package com.jobosint.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.BrowserChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.nio.file.Path;

@Configuration
@Slf4j
public class PlaywrightBrowser {

    private Browser browser;

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        return Playwright.create();
    }

    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        BrowserType chromium = playwright.chromium();
        if (browser == null) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(true);
            browser = chromium.launch(launchOptions);
            stopWatch.stop();
            log.info("Browser started in {} ms", stopWatch.getTotalTimeMillis());
        } else {
            log.info("Using existing browser instance");
        }
        return browser;
    }

    /*@Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {

        Path userDataDir = Path.of(System.getProperty("user.home"), "Library", "Application Support", "Google", "Chrome");

        BrowserType.LaunchPersistentContextOptions contextOptions = new BrowserType.LaunchPersistentContextOptions()
                .setChannel("chrome")
                .setHeadless(false);

        if (browser == null) {
            try (BrowserContext context = playwright.chromium().launchPersistentContext(userDataDir, contextOptions)) {
                browser = context.browser();
            }
        }

        return browser;
    }*/
}
