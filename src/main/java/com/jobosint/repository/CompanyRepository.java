package com.jobosint.repository;

import com.jobosint.model.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends ListCrudRepository<Company, UUID>, ListPagingAndSortingRepository<Company, UUID> {
    List<Company> findAllByOrderByName(Pageable pageable);

    List<Company> findCompaniesByNameContainingIgnoreCase(String name);

    List<Company> findCompaniesByNameIsIgnoreCase(String name);

    @Query("select * from company where linkedin_token = :token")
    Optional<Company> findCompanyByLinkedInToken(String token);
}
