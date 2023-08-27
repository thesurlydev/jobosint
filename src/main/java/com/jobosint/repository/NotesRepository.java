package com.jobosint.repository;

import com.jobosint.model.Notes;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NotesRepository extends CrudRepository<Notes, UUID> {


    @Query("""
            select * from jobosint.note as n where n.job = :jobId
            """)
    List<Notes> findByJobId(UUID jobId);
}
