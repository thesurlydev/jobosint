package com.jobosint.service;

import com.jobosint.repository.PartRepository;
import com.jobosint.repository.PriceRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final PartRepository partRepository;
    private final PriceRespository priceRespository;

    public void report() {
        log.info("Total parts: {}", partRepository.count());
        log.info("Total prices: {}", priceRespository.count());
    }
}
