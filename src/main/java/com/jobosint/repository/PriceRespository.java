package com.jobosint.repository;

import com.jobosint.model.Price;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PriceRespository extends CrudRepository<Price, UUID> {

    // get prices by part id
    List<Price> findAllByPartNumberOrderByVendorId(UUID partId);
}
