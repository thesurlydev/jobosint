package com.jobosint.repository;

import com.jobosint.model.AttributeValue;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeValueRepository extends CrudRepository<AttributeValue, UUID>,
        PagingAndSortingRepository<AttributeValue, UUID> {

    List<AttributeValue> findAllByAttributeId(UUID attributeId);

    @Query("""
    select av.value
    from jobosint.public.attribute_value av, jobosint.public.attribute a 
    where av.attribute = a.id and a.name = :name order by av.value;
""")
    List<String> findAllByAttributeName(String name);

}
