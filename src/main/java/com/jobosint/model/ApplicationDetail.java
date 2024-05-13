package com.jobosint.model;

import com.jobosint.util.DisplayUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record ApplicationDetail(@Id UUID id,
                                @Column("created_at") LocalDateTime createdAt,
                                @Column("applied_at") LocalDate appliedAt,
                                String status,
                                String notes,
                                String resume,

                                @Column("jobId") UUID jobId,
                                @Column("jobTitle") String jobTitle,
                                @Column("jobUrl") String jobUrl,
                                @Column("jobCreatedAt") LocalDateTime jobCreatedAt,
                                @Column("jobSalaryMin") String jobSalaryMin,
                                @Column("jobSalaryMax") String jobSalaryMax,
                                @Column("jobSource") String jobSource,
                                @Column("jobNotes") String jobNotes,
                                @Column("jobContent") String jobContent, // stored as markdown
                                @Column("jobStatus") String jobStatus,

                                @Column("companyId") UUID companyId,
                                @Column("companyName") String companyName,
                                @Column("companyWebsiteUrl") String companyWebsiteUrl,
                                @Column("companyStockTicker") String companyStockTicker,
                                @Column("companyEmployeeCount") String companyEmployeeCount,
                                @Column("companySummary") String companySummary,
                                @Column("companyLocation") String companyLocation
) {
    public String salaryDisplay() {
        return DisplayUtils.salaryDisplay(this.jobSalaryMin, this.jobSalaryMax);
    }
    public String jobTitleCompanyDisplay() {
        return this.companyName + ": " + this.jobTitle;
    }

    public String appliedAtDisplay() {
        if (this.appliedAt == null) {
            return "n/a";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        return this.appliedAt.format(formatter);
    }
}
