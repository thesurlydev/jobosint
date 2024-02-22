package com.jobosint.model.ai;

import java.util.List;

public record JobDescriptionParseResult(String jobTitle,
                                        String jobDescription,
                                        String companyName,
                                        String aboutCompany,
                                        String aboutTeam,
//                                        CompanyDetail companyDetail,
                                        List<String> qualifications,
//                                        List<String> technologies,
                                        String minimumSalary,
                                        String maximumSalary,
                                        List<String> responsibilities,
                                        List<String> interviewProcess,
                                        Integer numberOfApplicants,
                                        String location,
                                        List<String> benefits) {
}
