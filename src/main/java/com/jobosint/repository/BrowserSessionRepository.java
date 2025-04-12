package com.jobosint.repository;

import com.jobosint.model.browse.BrowserSession;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface BrowserSessionRepository extends ListCrudRepository<BrowserSession, UUID> {
}
