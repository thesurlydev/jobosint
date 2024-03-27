package com.jobosint.model;

import com.jobosint.util.DisplayUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationDetail(@Id UUID id,
                                @Column("created_at") LocalDateTime createdAt,
                                String status,
                                String notes,

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
        return this.jobTitle + " @" + this.companyName;
    }
}
