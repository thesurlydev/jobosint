package com.jobosint.repository;

import com.jobosint.model.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JobRepository extends CrudRepository<Job, UUID> {

}
