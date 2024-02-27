package com.jobosint.service;

import com.jobosint.model.Attribute;
import com.jobosint.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;

    public List<String> getSources() {
        return List.of("LinkedIn", "Google Jobs", "Indeed", "Recruiter", "Company Job Site");
    }

    public List<String> getStatuses() {
        return List.of("Applied", "Interviewing", "Rejected", "Received Offer");
    }

    public List<String> getAttributeValues(String name) {
        return attributeRepository.findAttributesByNameContainingIgnoreCase(name).stream()
                .map(Attribute::value)
                .sorted()
                .toList();
    }
}
