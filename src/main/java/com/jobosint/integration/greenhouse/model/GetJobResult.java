package com.jobosint.integration.greenhouse.model;

import com.jobosint.util.ConversionUtils;

import java.util.UUID;

public record GetJobResult(String boardToken, String id, Job job) {
    public com.jobosint.model.Job toJob(UUID companyId) {
        return new com.jobosint.model.Job(null, companyId, id, job.title(), job.absolute_url(), null,
                null, "Greenhouse", null, ConversionUtils.convertToMarkdown(job.content()), "Discovered", null);
    }

    public static GetJobResult fromJob(String boardToken, com.jobosint.model.Job job) {
        Job greenhouseJob = new Job(job.url(), Long.valueOf(job.jobBoardId()), job.title(), job.content(), null, null, null);
        return new GetJobResult(boardToken, greenhouseJob.id().toString(), greenhouseJob);
    }
}
