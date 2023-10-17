package com.jobosint.repository;

import com.jobosint.model.Price;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PriceRespository extends CrudRepository<Price, UUID> {
}
