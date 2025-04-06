package com.jobosint.ingest;

import com.jobosint.model.Company;
import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JobEnricher implements DocumentTransformer {

    private final JobDetail jobDetail;

    @Override
    public List<Document> apply(List<Document> documents) {

        Job job = jobDetail.job();
        Map<String, String> jobDetailMetadata;
        if (job != null) {
            jobDetailMetadata = new HashMap<>();
            if (job.title() != null ) {
                jobDetailMetadata.put("title", job.title());
            }
            if (job.salaryMax() != null) {
                jobDetailMetadata.put("salary_max", job.salaryMax());
            }
            if (job.salaryMin() != null) {
                jobDetailMetadata.put("salary_min", job.salaryMin());
            }
        } else {
            jobDetailMetadata = null;
        }

        Company company = jobDetail.company();
        Map<String, String> companyMetadata;
        if (company != null) {
            companyMetadata = new HashMap<>();
            if (company.name() != null) {
                companyMetadata.put("company_name", company.name());
            }
            if (company.employeeCount() != null) {
                companyMetadata.put("employee_count", company.employeeCount());
            }
        } else {
            companyMetadata = null;
        }

        documents.forEach(document -> {
            if (jobDetailMetadata != null) {
                document.getMetadata().putAll(jobDetailMetadata);
            }
            if (companyMetadata != null) {
                document.getMetadata().putAll(companyMetadata);
            }
        });

        return documents;
    }
}
