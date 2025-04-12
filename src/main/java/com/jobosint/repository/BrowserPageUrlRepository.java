package com.jobosint.repository;

import com.jobosint.model.browse.BrowserPageUrl;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface BrowserPageUrlRepository extends ListCrudRepository<BrowserPageUrl, UUID> {
}
