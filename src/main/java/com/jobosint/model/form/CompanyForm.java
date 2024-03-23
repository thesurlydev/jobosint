package com.jobosint.model.form;

import com.jobosint.model.Company;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public class CompanyForm {

    private UUID id;
    private String name;
    private String websiteUrl;
    private String stockTicker;
    private Long employeeCount;
    private String summary;
    private String location;

    public CompanyForm() {
    }

    public CompanyForm(UUID id, String name, String websiteUrl, String stockTicker, Long employeeCount, String summary, String location) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.stockTicker = stockTicker;
        this.employeeCount = employeeCount;
        this.summary = summary;
        this.location = location;
    }

    public static CompanyForm fromCompany(Company company) {
        return new CompanyForm(company.id(), company.name(), company.websiteUrl(), company.stockTicker(), company.employeeCount(), company.summary(), company.location());
    }
}
