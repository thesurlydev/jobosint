package com.jobosint.repository;

import com.jobosint.model.Application;
import com.jobosint.model.ApplicationDetail;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends ListCrudRepository<Application, UUID> {

    String APPLICATION_DETAIL_SELECT = """
            select a.*,
            j.id as jobId, j.title as jobTitle, j.url as jobUrl, j.created_at as jobCreatedAt, j.salary_min as jobSalaryMin, j.salary_max as jobSalaryMax, j.source as jobSource, j.notes as jobNotes, j.content as jobContent, j.status as jobStatus,
            c.id as companyId, c.name as companyName, c.website_url as companyWebsiteUrl, c.stock_ticker as companyStockTicker, c.employee_count as companyEmployeeCount, c.summary as companySummary, c.location as companyLocation
            from jobosint.public.application a
            inner join public.job j on j.id = a.job
            inner join public.company c on c.id = j.company
            """;

    @Query(APPLICATION_DETAIL_SELECT + "order by a.status, a.applied_at desc, c.name")
    List<ApplicationDetail> findAllApplicationDetailOrderByCreatedAt();

    @Query(APPLICATION_DETAIL_SELECT + "where a.id = :id")
    ApplicationDetail findApplicationDetailById(UUID id);

    @Modifying
    @Query("delete from jobosint.public.application where job = :jobId")
    void deleteAllByJobId(UUID jobId);
}
