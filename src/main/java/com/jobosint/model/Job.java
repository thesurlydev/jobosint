package com.jobosint.model;

import com.jobosint.convert.MarkdownToHtmlConverter;
import com.jobosint.model.form.JobForm;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public record Job(@Id UUID id,
                  @Column("company") UUID companyId,
                  String title,
                  String url,
                  @Column("created_at") LocalDateTime createdAt,
                  @Column("salary_min") String salaryMin,
                  @Column("salary_max") String salaryMax,
                  String source,
                  String notes,
                  String content, // stored as markdown
                  String status,
                  UUID page_id
) {

    public String htmlContent() {
        return MarkdownToHtmlConverter.convertToHtml(this.content);
    }

    // used by thymeleaf views
    @SuppressWarnings("unused")
    public String salaryDisplay() {
        if (this.salaryMin.isBlank() && this.salaryMax.isBlank()) {
            return "n/a";
        } else if (!this.salaryMin.isBlank() && this.salaryMax.isBlank()) {
            return this.salaryMin;
        } else {
            return new StringJoiner("-")
                    .add(String.valueOf(this.salaryMin))
                    .add(this.salaryMax)
                    .toString();
        }
    }

    public static Job fromJobWithNewStatus(Job job, String newStatus) {
        return new Job(job.id(),
                job.companyId(),
                job.title(),
                job.url(),
                job.createdAt(),
                job.salaryMin(),
                job.salaryMax(),
                job.source(),
                job.notes(),
                job.content(),
                newStatus,
                job.page_id()
        );
    }

    public static Job fromForm(JobForm form) {
        return new Job(form.getId(),
                form.getCompanyId(),
                form.getTitle(),
                form.getUrl(),
                LocalDateTime.now(),
                form.getSalaryMin(),
                form.getSalaryMax(),
                form.getSource(),
                form.getNotes(),
                null,
                form.getStatus(),
                null
        );
    }
}
