package com.jobosint.repository;

import com.jobosint.model.Attribute;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeRepository
        extends CrudRepository<Attribute, UUID>, PagingAndSortingRepository<Attribute, UUID> {

    @NotNull
    @Override List<Attribute> findAll();

    List<Attribute> findAllByOrderByName(Pageable pageable);

//    Optional<Attribute> findById(UUID id);

}
