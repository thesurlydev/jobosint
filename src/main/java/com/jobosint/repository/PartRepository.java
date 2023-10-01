package com.jobosint.repository;

import com.jobosint.model.Part;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PartRepository extends CrudRepository<Part, UUID> {
}
