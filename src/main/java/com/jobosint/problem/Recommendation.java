package com.jobosint.problem;

public record Recommendation(String title,
                             String description,
                             String complaintBeingSolve,
                             String businessModel,
                             SoftwareFormat softwareFormat,
                             Long totalAddressableMarket,
                             Long serviceableAvailableMarket,
                             Long serviceableObtainableMarket,
                             java.util.List<MarketValidationExample> marketValidationExamples) {
}
