package com.jobosint.event;

import com.jobosint.model.ext.Page;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class PageCreatedEvent extends ApplicationEvent {
    private final Page page;

    public PageCreatedEvent(Object source, Page page) {
        super(source);
        this.page = page;
    }
}
