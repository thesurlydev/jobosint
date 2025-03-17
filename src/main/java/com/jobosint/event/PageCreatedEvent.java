package com.jobosint.event;

import com.jobosint.model.Page;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class PageCreatedEvent extends ApplicationEvent {
    private final Page page;
    private final List<Map<String, String>> cookies;

    public PageCreatedEvent(Object source, Page page, List<Map<String, String>> cookies) {
        super(source);
        this.page = page;
        this.cookies = cookies;
    }
}
