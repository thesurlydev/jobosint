package com.jobosint.event;

import com.jobosint.model.Company;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class CompanyCreatedEvent  extends ApplicationEvent {
    private final Company company;

    public CompanyCreatedEvent(Object source, Company company) {
        super(source);
        this.company = company;
    }


    @Override
    public String toString() {
        return "CompanyCreatedEvent{" +
                "company=" + company +
                '}';
    }
}
