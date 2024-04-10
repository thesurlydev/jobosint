package com.jobosint.model;

import org.springframework.data.relational.core.mapping.Column;

import java.text.MessageFormat;
import java.util.UUID;

public record ContactDetail(UUID id,
                            String fullName,
                            String title,
                            @Column("linkedin_profile_url") String linkedInProfileUrl,
                            String notes,
                            String email,
                            String phoneNumber,

                            @Column("company_id") UUID companyId,
                            @Column("name") String companyName,
                            @Column("website_url") String companyWebsiteUrl,
                            @Column("location") String companyLocation,
                            @Column("summary") String companySummary,
                            @Column("stock_ticker") String companyStockTicker,
                            @Column("employee_count") String companyEmployeeCount) {

    public String nameTitleDisplay() {
        return MessageFormat.format("{0}, {1}",
                fullName,
                title);
    }

    public String phoneNumberDisplay() {
        if (phoneNumber == null) {
            return null;
        }
        if (phoneNumber.length() != 10) {
            return phoneNumber;
        }
        return MessageFormat.format("({0}) {1}-{2}",
                phoneNumber.substring(0, 3),
                phoneNumber.substring(3, 6),
                phoneNumber.substring(6));
    }
}
