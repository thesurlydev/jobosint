package com.jobosint.listener;

import com.jobosint.event.DownloadImageEvent;
import com.jobosint.service.DownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadImageEventListener implements ApplicationListener<DownloadImageEvent> {

    private final DownloadService downloadService;


    @Override
    public void onApplicationEvent(DownloadImageEvent event) {
        log.info(event.toString());
        downloadService.downloadImage(event.getDownloadImageRequest());
    }
}
