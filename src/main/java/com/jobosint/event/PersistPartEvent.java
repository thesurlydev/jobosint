package com.jobosint.event;

import com.jobosint.model.Part;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class PersistPartEvent extends ApplicationEvent {

    private final Part part;

    public PersistPartEvent(Object source, Part part) {
        super(source);
        this.part = part;
    }
}
