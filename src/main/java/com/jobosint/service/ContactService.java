package com.jobosint.service;

import com.jobosint.model.Contact;
import com.jobosint.model.ContactDetail;
import com.jobosint.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public Optional<Contact> getContact(UUID id) {
        return contactRepository.findById(id);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<ContactDetail> getAllContactDetailOrderByName() {
        return contactRepository.findAllContactDetailOrderByFullName();
    }

    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }
}
