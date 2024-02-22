package com.jobosint.model.ai;

import java.util.List;

public record JobDescriptionParseResult(String jobTitle,
                                        String jobDescription,
                                        String aboutCompany,
                                        CompanyDetail companyDetail,
                                        List<String> requirements,
                                        String minimumSalary,
                                        String maximumSalary,
                                        List<String> reasonsToJoin) {
}
