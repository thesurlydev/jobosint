package com.jobosint.repository;

import com.jobosint.model.Page;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PageRepository extends CrudRepository<Page, UUID> {
}
