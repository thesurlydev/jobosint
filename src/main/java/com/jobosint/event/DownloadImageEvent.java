package com.jobosint.event;

import com.jobosint.model.DownloadImageRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DownloadImageEvent extends ApplicationEvent {

    private final DownloadImageRequest downloadImageRequest;

    public DownloadImageEvent(Object source, DownloadImageRequest downloadImageRequest) {
        super(source);
        this.downloadImageRequest = downloadImageRequest;
    }
}
