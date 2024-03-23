package com.jobosint.service;

import com.jobosint.model.Attribute;
import com.jobosint.model.AttributeDetail;
import com.jobosint.model.AttributeValue;
import com.jobosint.repository.AttributeRepository;
import com.jobosint.repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    public List<String> findAttributeValuesByName(String name) {
        return attributeValueRepository.findAllByAttributeName(name);
    }

    public List<String> getSources() {
        return findAttributeValuesByName("job-source");
    }

    public List<String> getJobStatuses() {
        return findAttributeValuesByName("job-status");
    }

    public List<String> getApplicationStatuses() {
        return findAttributeValuesByName("application-status");
    }

    public List<Attribute> getAllAttributes() {
//        Pageable pageable = Pageable.ofSize(100);
        return attributeRepository.findAll();
    }

    public Optional<AttributeDetail> getAttributeDetail(UUID attributeId) {
        Optional<Attribute> maybeAttribute = attributeRepository.findById(attributeId);
        if (maybeAttribute.isPresent()) {
            Attribute attr = maybeAttribute.get();
            Set<AttributeValue> values = attr.attributeValues();
            return Optional.of(new AttributeDetail(attr, values));
        }
        return Optional.empty();
    }

    @Transactional
    public Attribute saveAttribute(Attribute attribute) {
        var persistedAttr = attributeRepository.save(attribute);
        /*if (company.id() == null) {
            applicationEventPublisher.publishEvent(new AttributeCreatedEvent(this, persistedAttr));
        }*/
        return persistedAttr;
    }

    @Transactional
    public Iterable<AttributeValue> saveAttributeValues(Set<AttributeValue> values) {
        return attributeValueRepository.saveAll(values);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        attributeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAttributeValue(UUID id) {
        attributeValueRepository.deleteById(id);
    }
}
