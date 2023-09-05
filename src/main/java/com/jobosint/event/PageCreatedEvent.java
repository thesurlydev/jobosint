package com.jobosint.event;

import com.jobosint.model.ext.Page;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PageCreatedEvent extends ApplicationEvent {
    private final Page page;

    public PageCreatedEvent(Object source, Page page) {
        super(source);
        this.page = page;
    }



    @Override
    public String toString() {
        return "PageCreatedEvent{" +
                "id=" + page.id() +
                ", url=" + page.url() +
                ", src=" + page.source() +
                '}';
    }
}
