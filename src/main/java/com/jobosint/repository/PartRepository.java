package com.jobosint.repository;

import com.jobosint.model.Part;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PartRepository extends CrudRepository<Part, UUID> {

    @Query("""
            select *
            from jobosint.part p
            order by p.title
              """)
    List<Part> findAllPartsOrderByTitle();
}
