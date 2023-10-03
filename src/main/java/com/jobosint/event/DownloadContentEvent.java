package com.jobosint.event;

import com.jobosint.model.DownloadContentRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
@EqualsAndHashCode
public class DownloadContentEvent extends ApplicationEvent {

    private final DownloadContentRequest downloadContentRequest;

    public DownloadContentEvent(Object source, DownloadContentRequest downloadContentRequest) {
        super(source);
        this.downloadContentRequest = downloadContentRequest;
    }
}
