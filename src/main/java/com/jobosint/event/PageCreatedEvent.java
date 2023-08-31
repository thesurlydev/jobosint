package com.jobosint.event;

import com.jobosint.model.ext.Page;
import org.springframework.context.ApplicationEvent;

public class PageCreatedEvent extends ApplicationEvent {
    private final Page page;

    public PageCreatedEvent(Object source, Page page) {
        super(source);
        this.page = page;
    }

    @Override
    public String toString() {
        return "PageCreatedEvent{" +
                "page=" + page +
                '}';
    }
}
