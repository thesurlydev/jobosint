package com.jobosint.service;

import com.jobosint.model.Attribute;
import com.jobosint.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;

    public List<String> findAttributeValuesByName(String name) {
        return attributeRepository.findAttributeByName(name).values().stream().sorted().toList();
    }

    public List<String> getEventTypes() {
        return findAttributeValuesByName("event-type");
    }

    public List<String> getInterviewTypes() {
        return findAttributeValuesByName("interview-type");
    }

    public List<String> getSources() {
        return findAttributeValuesByName("job-source");
    }

    public List<String> getJobStatuses() {
        return findAttributeValuesByName("job-status");
    }

    public List<String> getJobTitleExcludes() {
        return findAttributeValuesByName("job-title-exclude");
    }

    public List<String> getApplicationStatuses() {
        return findAttributeValuesByName("application-status");
    }

    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    @Transactional
    public Attribute saveAttribute(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        attributeRepository.deleteById(id);
    }

}
