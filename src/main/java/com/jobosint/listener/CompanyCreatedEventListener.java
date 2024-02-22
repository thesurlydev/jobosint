package com.jobosint.listener;

import com.jobosint.event.CompanyCreatedEvent;
import com.jobosint.model.Company;
import com.jobosint.model.ai.CompanyDetail;
import com.jobosint.service.CompanyService;
import com.jobosint.service.ai.CompanyDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
@Slf4j
public class CompanyCreatedEventListener implements ApplicationListener<CompanyCreatedEvent> {

    private final CompanyDetailsService companyDetailsService;
    private final CompanyService companyService;


    /*@Override
    public void onApplicationEvent(@NonNull CompanyCreatedEvent event) {

        try {
            CompanyDetail detail = companyDetailsService.getCompanyDetails(event.getCompany().name());
            companyService.getById(event.getCompany().id())
                    .ifPresent(existingCompany -> {
                        Long numberOfEmployees = ofNullable(existingCompany.employeeCount()).orElse(detail.numberOfEmployees());
                        String ticker = ofNullable(existingCompany.stockTicker()).orElse(detail.stockTicker());
                        String website = ofNullable(existingCompany.websiteUrl()).orElse(detail.websiteLink());
                        String summary = ofNullable(existingCompany.summary()).orElse(detail.summary());
                        String location = ofNullable(existingCompany.location()).orElse(detail.location());

                        Company updatedCompany = new Company(existingCompany.id(), existingCompany.name(),
                                website, ticker, numberOfEmployees, summary, location);

                        log.info("Updating company: {}", updatedCompany);
                        companyService.saveCompany(updatedCompany);
                    });

            log.info("Received and processed CompanyCreatedEvent: {}", event);
        } catch (Exception e) {
            log.error("Error processing CompanyCreatedEvent: {}", event, e);
            // Handle the error appropriately
        }
    }*/

    @Override
    public void onApplicationEvent(@NonNull CompanyCreatedEvent event) {
        log.info("Received: {}", event);
        try {
            CompanyDetail detail = companyDetailsService.getCompanyDetails(event.getCompany().name());
            companyService.getById(event.getCompany().id()).ifPresent(existingCompany -> {
                Long numberOfEmployees = getNonNullOrDefault(existingCompany.employeeCount(), detail.numberOfEmployees());
                String ticker = getNonNullOrEmptyOrDefault(existingCompany.stockTicker(), detail.stockTicker());
                String website = getNonNullOrEmptyOrDefault(existingCompany.websiteUrl(), detail.websiteLink());
                String summary = getNonNullOrEmptyOrDefault(existingCompany.summary(), detail.summary());
                String location = getNonNullOrEmptyOrDefault(existingCompany.location(), detail.location());

                Company updatedCompany = new Company(existingCompany.id(), existingCompany.name(),
                        website, ticker, numberOfEmployees, summary, location);

                log.info("Updating company: {}", updatedCompany);
                companyService.saveCompany(updatedCompany);
            });

            log.info("Received and processed CompanyCreatedEvent: {}", event);
        } catch (Exception e) {
            log.error("Error processing CompanyCreatedEvent: {}", event, e);
            // Handle the error appropriately
        }
    }

    private String getNonNullOrEmptyOrDefault(String existingValue, String defaultValue) {
        return Optional.ofNullable(existingValue).filter(str -> !str.isEmpty()).orElse(defaultValue);
    }

    private <T> T getNonNullOrDefault(T existingValue, T defaultValue) {
        return Optional.ofNullable(existingValue).orElse(defaultValue);
    }
}


