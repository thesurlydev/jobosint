package com.jobosint.service;

import com.jobosint.event.CompanyCreatedEvent;
import com.jobosint.event.CompanyDeletedEvent;
import com.jobosint.model.Company;
import com.jobosint.repository.CompanyRepository;
import com.jobosint.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;

    public Optional<Company> getCompanyByLinkedInToken(String token) {
        return companyRepository.findCompanyByLinkedInToken(token);
    }

    public List<Company> search(String query) {
        if (query.startsWith("\"") && query.endsWith("\"")) {
            var exact = query.replaceAll("\"", "");
            log.info("Performing exact search for: {}", exact);
            return companyRepository.findCompaniesByNameIsIgnoreCase(exact);
        }
        var results = companyRepository.findCompaniesByNameIsIgnoreCase(query);
        if (results.size() == 1) {
            log.info("Found exact match");
            return results;
        }
        log.info("No exact match; performing 'like' search for: {}", query);
        return companyRepository.findCompaniesByNameContainingIgnoreCase(query);
    }

    @Transactional
    public Company saveCompany(Company company) {
        var persistedCompany = companyRepository.save(company);
        if (company.id() == null) {
            applicationEventPublisher.publishEvent(new CompanyCreatedEvent(this, persistedCompany));
        }
        return persistedCompany;
    }

    public Company upsertCompany(String companyName, Company companyCandidate) {
        Company company = null;
        if (companyName.isBlank()) {
            log.warn("Using N/A for company");
            List<Company> companies = search("N/A");
            company = companies.getFirst();
        } else {
            List<Company> companies = search("\"" + companyName + "\"");
            if (companies.isEmpty()) {
                Company c = companyCandidate;
                if (c == null) {
                    c = new Company(null, companyName, null, null,
                            null, null, null, null, null);
                }
                log.info("Creating new company: {}", c);
                company = saveCompany(c);
            } else if (companies.size() == 1) {
                if (companyCandidate != null) {
                    // merge
                    Company existingCompany = companies.getFirst();
                    company = mergeCompany(existingCompany, companyCandidate);
                } else {
                    company = companies.getFirst();
                }

            } else {
                log.error("Ambiguous company name: {}", companyName);
            }
        }
        return company;
    }

    public Company mergeCompany(Company company1, Company company2) {
        UUID id = company1.id();
        String name = company1.name() != null ? company1.name() : company2.name();
        String websiteUrl = company1.websiteUrl() != null ? company1.websiteUrl() : company2.websiteUrl();
        String stockTicker = company1.stockTicker() != null ? company1.stockTicker() : company2.stockTicker();
        String employeeCount = company1.employeeCount() != null ? company1.employeeCount() : company2.employeeCount();
        String summary = company1.summary() != null ? company1.summary() : company2.summary();
        String location = company1.location() != null ? company1.location() : company2.location();
        String linkedinToken = company1.linkedinToken() != null ? company1.linkedinToken() : company2.linkedinToken();
        String greenhouseToken = company1.greenhouseToken() != null ? company1.greenhouseToken() : company2.greenhouseToken();

        Company mergedCompany = new Company(id, name, websiteUrl, stockTicker, employeeCount, summary, location, linkedinToken, greenhouseToken);

        return companyRepository.save(mergedCompany);
    }

    @Transactional
    public void deleteCompany(UUID id) {
        // delete any associated jobs first
        jobRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new CompanyDeletedEvent(this, id));
    }

    public List<Company> getAllSorted() {
        Pageable pageable = Pageable.ofSize(999);
        return companyRepository.findAllByOrderByName(pageable);
    }

    public Optional<Company> getById(UUID id) {
        return companyRepository.findById(id);
    }
}
