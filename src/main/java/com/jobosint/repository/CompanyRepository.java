package com.jobosint.repository;

import com.jobosint.model.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends CrudRepository<Company, UUID> {
    List<Company> findByOrderByNameAsc();
}
