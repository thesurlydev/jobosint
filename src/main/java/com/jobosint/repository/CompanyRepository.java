package com.jobosint.repository;

import com.jobosint.model.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends CrudRepository<Company, UUID>, PagingAndSortingRepository<Company, UUID> {
    List<Company> findAllByOrderByName(Pageable pageable);

    List<Company> findCompaniesByNameContainingIgnoreCase(String name);

    List<Company> findCompaniesByNameIsIgnoreCase(String name);
}
