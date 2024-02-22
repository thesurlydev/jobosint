package com.jobosint.model.ai;

public record CompanyDetail(String name,
                            String websiteLink,
                            String stockTicker,
                            Long numberOfEmployees,
                            String summary,
                            String location
) {}
