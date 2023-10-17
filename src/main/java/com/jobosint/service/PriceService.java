package com.jobosint.service;

import com.jobosint.model.Price;
import com.jobosint.repository.PriceRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PriceService {
    private final PriceRespository priceRespository;

    public Price savePrice(Price price) {

        log.info("Saving price: {}", price);
        Price pp = null;
        try {
            pp = priceRespository.save(price);
        } catch (Exception e) {
            if (e.getCause() instanceof DuplicateKeyException) {
                log.warn("Duplicate: {}", price);
            } else {
                log.error("Error saving price", e);
            }
        }
        return pp;
    }
}
