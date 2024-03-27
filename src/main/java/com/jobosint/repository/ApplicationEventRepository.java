package com.jobosint.repository;

import com.jobosint.model.ApplicationEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationEventRepository extends CrudRepository<ApplicationEvent, UUID> {
    @NotNull
    @Override
    List<ApplicationEvent> findAll();
}
