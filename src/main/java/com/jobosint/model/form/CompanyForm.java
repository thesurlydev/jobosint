package com.jobosint.model.form;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CompanyForm {

    private UUID id;
    private String name;
    private String websiteUrl;

    public CompanyForm() {
    }

    public CompanyForm(UUID id, String name, String websiteUrl) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
