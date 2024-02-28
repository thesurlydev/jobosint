package com.jobosint.repository;

import com.jobosint.model.AttributeValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeValueRepository extends CrudRepository<AttributeValue, UUID>,
        PagingAndSortingRepository<AttributeValue, UUID> {

    List<AttributeValue> findAllByAttributeId(UUID attributeId);
}
