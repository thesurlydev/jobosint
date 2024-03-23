package com.jobosint.repository;

import com.jobosint.model.Application;
import com.jobosint.model.ApplicationDetail;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends CrudRepository<Application, UUID> {

    @Query("""
            select a.*, c.id as company_id, c.name, c.website_url, c.location, c.summary, c.stock_ticker, c.employee_count
                      from jobosint.public.application a, jobosint.public.company c
                      where a.company = c.id
                      order by a.created_at
            """)
    List<ApplicationDetail> findAllApplicationDetailOrderByCreatedAt();
}
