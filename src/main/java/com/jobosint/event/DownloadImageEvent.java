package com.jobosint.event;

import com.jobosint.model.DownloadImageRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
@EqualsAndHashCode
public class DownloadImageEvent extends ApplicationEvent {

    private final DownloadImageRequest downloadImageRequest;

    public DownloadImageEvent(Object source, DownloadImageRequest downloadImageRequest) {
        super(source);
        this.downloadImageRequest = downloadImageRequest;
    }
}
