package com.jobosint.event;

import com.jobosint.model.Job;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
@Slf4j
public class JobCreatedEvent extends ApplicationEvent {
    private final Job job;

    public JobCreatedEvent(Object source, Job job) {
        super(source);
        this.job = job;
    }
}
