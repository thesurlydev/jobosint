package com.jobosint.repository;

import com.jobosint.model.JobKeyword;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface JobKeywordRepository extends ListCrudRepository<JobKeyword, UUID> {
}
