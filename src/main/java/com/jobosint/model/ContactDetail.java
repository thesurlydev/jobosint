package com.jobosint.model;

import java.text.MessageFormat;
import java.util.UUID;

public record ContactDetail(UUID id,
                            String fullName,
                            String title,
                            String linkedInProfileUrl,
                            String notes,
                            String email,
                            String phoneNumber,

                            UUID companyId,
                            String companyName,
                            String companyWebsiteUrl,
                            String companyLocation,
                            String companySummary,
                            String companyStockTicker,
                            String companyEmployeeCount) {

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
