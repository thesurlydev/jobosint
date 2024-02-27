package com.jobosint.repository;

import com.jobosint.model.Attribute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeRepository extends CrudRepository<Attribute, UUID>, PagingAndSortingRepository<Attribute,
        UUID> {

    List<Attribute> findAttributesByNameContainingIgnoreCase(String name);
}
