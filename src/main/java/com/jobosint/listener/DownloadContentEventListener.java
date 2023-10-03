package com.jobosint.listener;

import com.jobosint.event.DownloadContentEvent;
import com.jobosint.event.DownloadImageEvent;
import com.jobosint.service.DownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadContentEventListener implements ApplicationListener<DownloadContentEvent> {

    private final DownloadService downloadService;

    @Override
    public void onApplicationEvent(DownloadContentEvent event) {
        log.info(event.toString());
        try {
            downloadService.downloadContent(event.getDownloadContentRequest());
        } catch (InterruptedException | URISyntaxException | IOException e) {
            log.error("Error downloading " + event, e);
        }
    }
}
