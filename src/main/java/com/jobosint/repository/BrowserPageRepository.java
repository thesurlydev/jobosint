package com.jobosint.repository;

import com.jobosint.model.browse.BrowserPage;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface BrowserPageRepository extends ListCrudRepository<BrowserPage, UUID> {
}
