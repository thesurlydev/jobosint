package com.jobosint.repository;

import com.jobosint.model.Job;
import com.jobosint.model.JobDetail;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends CrudRepository<Job, UUID> {

    @Query("""
            select j.*, c.id as companyId, c.name, c.website_url
                      from jobosint.jobosint.job j, jobosint.jobosint.company c
                      where j.company = c.id
                      order by j.created_at
            """)
    List<JobDetail> findAllJobDetailOrderByCreatedAt();

    @Query("""
            select j.*, c.id as companyId, c.name, c.website_url
                      from jobosint.jobosint.job j, jobosint.jobosint.company c
                      where j.company = c.id and j.id = :id
                    
            """)
    JobDetail findJobDetailbyId(UUID id);


    @Modifying
    @Query("""
            delete from jobosint.job where company = :companyId
            """)
    void deleteAllByCompanyId(UUID companyId);
}
