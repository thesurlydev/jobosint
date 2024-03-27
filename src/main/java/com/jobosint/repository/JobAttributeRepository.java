package com.jobosint.repository;

import com.jobosint.model.JobAttribute;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JobAttributeRepository extends CrudRepository<JobAttribute, UUID> {
}
