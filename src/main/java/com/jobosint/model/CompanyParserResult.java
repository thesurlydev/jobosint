package com.jobosint.model;

public record CompanyParserResult(String name,
                                  String websiteUrl,
                                  String summary,
                                  String employeeCount,
                                  String industry,
                                  String location) {
}
