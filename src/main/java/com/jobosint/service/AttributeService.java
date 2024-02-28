package com.jobosint.service;

import com.jobosint.model.Attribute;
import com.jobosint.model.AttributeDetail;
import com.jobosint.model.AttributeValue;
import com.jobosint.repository.AttributeRepository;
import com.jobosint.repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<String> getSources() {
        return List.of("LinkedIn", "Google Jobs", "Indeed", "Recruiter", "Company Job Site");
    }

    public List<String> getStatuses() {
        return List.of("Applied", "Interviewing", "Rejected", "Received Offer");
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
}
