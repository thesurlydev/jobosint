package com.jobosint.model.form;

import com.jobosint.model.Company;
import lombok.Data;

import java.util.UUID;

@Data
public class CompanyForm {

    private UUID id;
    private String name;
    private String websiteUrl;
    private String stockTicker;
    private String employeeCount;
    private String summary;
    private String location;
    private String linkedinToken;
    private String greenhouseToken;

    public CompanyForm() {
    }

    public CompanyForm(UUID id, String name, String websiteUrl, String stockTicker, String employeeCount, String summary, String location, String linkedinToken, String greenhouseToken) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.stockTicker = stockTicker;
        this.employeeCount = employeeCount;
        this.summary = summary;
        this.location = location;
        this.linkedinToken = linkedinToken;
        this.greenhouseToken = greenhouseToken;
    }

    public static CompanyForm fromCompany(Company company) {
        return new CompanyForm(company.id(), company.name(), company.websiteUrl(), company.stockTicker(),
                company.employeeCount(), company.summary(), company.location(), company.linkedinToken(),
                company.greenhouseToken());
    }
}
