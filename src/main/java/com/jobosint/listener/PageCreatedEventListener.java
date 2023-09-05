package com.jobosint.listener;

import com.jobosint.event.PageCreatedEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class PageCreatedEventListener implements ApplicationListener<PageCreatedEvent> {
    @Override
    public void onApplicationEvent(@NonNull PageCreatedEvent event) {
        log.info("Received: {}", event);

        var filePath = Path.of("tester.html");
        try {
            Files.writeString(filePath, event.getPage().content());
            log.info("Wrote {}", filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
