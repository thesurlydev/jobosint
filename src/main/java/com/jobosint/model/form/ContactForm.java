package com.jobosint.model.form;

import com.jobosint.model.Contact;
import lombok.Data;

import java.util.UUID;

@Data
public class ContactForm {
    private UUID id;
    private UUID companyId;
    private String name;
    private String email;
    private String phoneNumber;
    private String title;
    private String notes;
    private String linkedInProfileUrl;

    public Contact toContact() {
        return new Contact(id, companyId, name, linkedInProfileUrl, title, notes, email, phoneNumber);
    }

    public ContactForm() {
    }

    public ContactForm(Contact contact) {
        this.id = contact.id();
        this.companyId = contact.companyId();
        this.name = contact.fullName();
        this.email = contact.email();
        this.phoneNumber = contact.phoneNumber();
        this.title = contact.title();
        this.notes = contact.notes();
        this.linkedInProfileUrl = contact.linkedInProfileUrl();
    }
}
