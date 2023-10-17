package com.jobosint.event;

import com.jobosint.model.VendorPart;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class PersistVendorPartEvent extends ApplicationEvent {

    private final VendorPart part;

    public PersistVendorPartEvent(Object source, VendorPart part) {
        super(source);
        this.part = part;
    }
}
