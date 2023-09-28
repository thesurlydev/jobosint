package com.jobosint.enrich;

import com.jobosint.config.CrunchbaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CrunchBaseService {

    private final CrunchbaseConfig crunchBaseConfig;

    public void test() {
        // TODO
    }
}
