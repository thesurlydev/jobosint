package com.jobosint.event;

import com.jobosint.model.browse.BrowserPageUrl;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class BrowserPageUrlCreatedEvent extends ApplicationEvent {

    private final BrowserPageUrl browserPageUrl;

    public BrowserPageUrlCreatedEvent(Object source, BrowserPageUrl browserPageUrl) {
        super(source);
        this.browserPageUrl = browserPageUrl;
    }
}
