package com.jobosint.repository;

import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import com.jobosint.model.JobPageDetail;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends ListCrudRepository<Job, UUID> {

    String JOB_DETAIL_SELECT = """
            select j.*,
            c.id as company_id,
            c.name,
            c.website_url,
            c.location,
            c.summary,
            c.stock_ticker,
            c.employee_count,
            ja.cloud_providers,
            ja.cloud_services,
            ja.culture_values,
            ja.databases,
            ja.frameworks,
            ja.interview_steps,
            ja.required_qualifications,
            ja.preferred_qualifications,
            ja.programming_languages,
            a.applied_at
            from jobosint.public.job j
            INNER JOIN jobosint.public.company c on j.company = c.id
            LEFT OUTER JOIN jobosint.public.job_attribute ja on ja.job = j.id
            LEFT OUTER JOIN jobosint.public.application a on a.job = j.id
            """;

    String JOB_PAGE_SELECT = """
        select j.id,
                j.page_id,
                j.job_board_id,
                j.source,
                p.url as page_url
         from jobosint.public.job j
                  INNER JOIN jobosint.public.page p on j.page_id = p.id
         where job_board_id is null and j.source = :source
    """;

    @Query(JOB_DETAIL_SELECT + " where j.status = 'Active' or j.status = 'Discovered' order by j.status, j.created_at desc")
    List<JobDetail> findAllJobDetailOrderByCreatedAt();

    @Query(JOB_DETAIL_SELECT + " where j.id = :id")
    Optional<JobDetail> findJobDetailbyId(UUID id);

    @Query(JOB_DETAIL_SELECT + " where c.id = :id")
    List<JobDetail> findJobsbyCompanyId(UUID id);

    @Modifying
    @Query("delete from jobosint.public.job where company = :companyId")
    void deleteAllByCompanyId(UUID companyId);

    @Query(JOB_DETAIL_SELECT + " where (title ilike '%' || :query || '%' or content ilike '%' || :query || '%')")
    List<JobDetail> searchJobs(String query);

    @Query("select * from jobosint.public.job where job.url = :url")
    Optional<Job> findJobByUrl(String url);

    @Query("select * from jobosint.public.job where job.source = :source and job.job_board_id = :jobId")
    Optional<Job> findJobBySourceJobId(String source, String jobId);

    @Query(JOB_PAGE_SELECT)
    List<JobPageDetail> findAllJobPageDetail(String source);

    @Modifying
    @Query("update jobosint.public.job set job_board_id = :jobBoardId where id = :id")
    void updateJobBoardId(UUID id, String jobBoardId);
}
