package com.jobosint.repository;

import com.jobosint.model.Note;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends CrudRepository<Note, UUID> {


    @Query("""
            select * from jobosint.note as n where n.job = :jobId
            """)
    List<Note> findByJobId(UUID jobId);
}
