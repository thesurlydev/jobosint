package com.jobosint.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.text.MessageFormat;
import java.util.UUID;

public record Contact(@Id UUID id,
                      @Column("company") UUID companyId,
                      @Column("full_name") String fullName,
                      @Column("linkedin_profile_url") String linkedInProfileUrl,
                      String title,
                      String notes,
                      String email,
                      @Column("phone_number") String phoneNumber
) {

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
