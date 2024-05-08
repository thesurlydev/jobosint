package com.jobosint.model;

import com.jobosint.model.form.JobForm;
import com.jobosint.util.ConversionUtils;
import com.jobosint.util.DisplayUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record Job(@Id UUID id,
                  @Column("company") UUID companyId,
                  @Column("job_board_id") String jobBoardId,
                  String title,
                  String url,
                  @Column("salary_min") String salaryMin,
                  @Column("salary_max") String salaryMax,
                  String source,
                  String notes,
                  String content, // stored as markdown
                  String status,
                  @Column("page_id") UUID pageId,
                  @Column("created_at") LocalDateTime createdAt
) {

    public String createdAtDisplay() {
        if (this.createdAt == null) {
            return "n/a";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd");
        return this.createdAt.format(formatter);
    }

    public String htmlContent() {
        return ConversionUtils.convertToHtml(this.content);
    }

    public String salaryDisplay() {
        return DisplayUtils.salaryDisplay(this.salaryMin, this.salaryMax);
    }

    public static Job fromJobWithNewStatus(Job job, String newStatus) {
        return new Job(job.id(),
                job.companyId(),
                job.jobBoardId(),
                job.title(),
                job.url(),
                job.salaryMin(),
                job.salaryMax(),
                job.source(),
                job.notes(),
                job.content(),
                newStatus,
                job.pageId(),
                job.createdAt()
        );
    }

    public static Job fromForm(JobForm form) {
        return new Job(form.getId(),
                form.getCompanyId(),
                form.getJobBoardId(),
                form.getTitle(),
                form.getUrl(),
                form.getSalaryMin(),
                form.getSalaryMax(),
                form.getSource(),
                form.getNotes(),
                form.getContent(),
                form.getStatus(),
                form.getPageId(),
                form.getCreatedAt()
        );
    }
}
