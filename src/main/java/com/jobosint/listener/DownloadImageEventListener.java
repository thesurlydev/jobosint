package com.jobosint.listener;

import com.jobosint.event.DownloadImageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DownloadImageEventListener implements ApplicationListener<DownloadImageEvent> {

    @Override
    public void onApplicationEvent(DownloadImageEvent event) {

    }
}
