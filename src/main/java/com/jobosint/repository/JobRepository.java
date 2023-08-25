package com.jobosint.repository;

import com.jobosint.model.Job;
import com.jobosint.model.JobAndCompany;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends CrudRepository<Job, UUID> {

    @Query("""
            select j.*, c.id as companyId, c.name as companyName, c.url as companyUrl
                      from jobosint.job j, jobosint.company c
                      where j.company = c.id
            """)
    List<JobAndCompany> findAllJobAndCompany();
}
