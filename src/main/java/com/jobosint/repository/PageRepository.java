package com.jobosint.repository;

import com.jobosint.model.Page;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface PageRepository extends ListCrudRepository<Page, UUID> {
}
