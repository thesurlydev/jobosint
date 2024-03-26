package com.jobosint.repository;

import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends CrudRepository<Job, UUID> {

    String JOB_DETAIL_SELECT = """
    select j.*, c.id as company_id, c.name, c.website_url, c.location, c.summary, c.stock_ticker, c.employee_count
    from jobosint.public.job j, jobosint.public.company c
    where j.company = c.id
    """;

    @Query(JOB_DETAIL_SELECT + "and j.status != 'Archived' order by j.created_at")
    List<JobDetail> findAllJobDetailOrderByCreatedAt();

    @Query(JOB_DETAIL_SELECT + " and j.id = :id")
    Optional<JobDetail> findJobDetailbyId(UUID id);

    @Modifying
    @Query("delete from jobosint.public.job where company = :companyId")
    void deleteAllByCompanyId(UUID companyId);

    @Query(JOB_DETAIL_SELECT + " and (title ilike '%' || :query || '%' or content ilike '%' || :query || '%')")
    List<JobDetail> searchJobs(String query);

}
