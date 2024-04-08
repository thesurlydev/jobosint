package com.jobosint.repository;

import com.jobosint.model.JobAttribute;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface JobAttributeRepository extends ListCrudRepository<JobAttribute, UUID> {
}
