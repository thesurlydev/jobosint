package com.jobosint.repository;

import com.jobosint.model.Application;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ApplicationRepository extends CrudRepository<Application, UUID> {
}
