package com.jobosint.repository;

import com.jobosint.model.Part;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@Table(name = "parts")
public interface PartRepository extends CrudRepository<Part, UUID> {

    @Query("""
            select *
            from parts p
            order by p.name
              """)
    List<Part> findAllPartsOrderByTitle();
}
