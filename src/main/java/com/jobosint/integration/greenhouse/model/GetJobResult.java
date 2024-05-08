package com.jobosint.integration.greenhouse.model;

import com.jobosint.model.SalaryRange;

import java.util.UUID;

public record GetJobResult(String boardToken, String id, Job job, SalaryRange salaryRange) {
    public com.jobosint.model.Job toJob(UUID companyId) {
        return new com.jobosint.model.Job(null, companyId, id, job.title(), job.absolute_url(), salaryRange.min(),
                salaryRange.max(), "Greenhouse", null, job.content(), "Discovered", null, null);
    }

    public static GetJobResult fromJob(String boardToken, com.jobosint.model.Job job) {
        Job greenhouseJob = new Job(job.url(), Long.valueOf(job.jobBoardId()), job.title(), job.content(), null, null, null);
        SalaryRange salaryRange = new SalaryRange(job.salaryMin(), job.salaryMax());
        return new GetJobResult(boardToken, greenhouseJob.id().toString(), greenhouseJob, salaryRange);
    }
}
