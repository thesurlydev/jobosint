package com.jobosint.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeService {

    public List<String> getSources() {
        return List.of("LinkedIn", "Google Jobs", "Indeed", "Recruiter", "Company Job Site");
    }

    public List<String> getStatuses() {
        return List.of("Applied", "Interviewing", "Rejected", "Received Offer");
    }
}
