package com.jobosint.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class CompanyDeletedEvent extends ApplicationEvent {
    private final UUID id;

    public CompanyDeletedEvent(Object source, UUID id) {
        super(source);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CompanyDeletedEvent{" +
                "id=" + id +
                '}';
    }
}
