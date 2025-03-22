package com.jobosint.repository;

import com.jobosint.model.ResumeKeyword;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface ResumeKeywordRepository extends ListCrudRepository<ResumeKeyword, UUID> {
}
