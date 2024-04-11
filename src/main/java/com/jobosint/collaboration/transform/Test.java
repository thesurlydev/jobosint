package com.jobosint.collaboration.transform;

import com.jobosint.model.GoogleSearchResponse;
import com.jobosint.model.ScrapeRequest;

import java.lang.reflect.Method;
import java.util.List;

public class Test implements BeanTransformer<GoogleSearchResponse, List<ScrapeRequest>> {
    @Override
    public List<ScrapeRequest> transform(GoogleSearchResponse input) {
        // Tool(A) -> A -> transform -> B -> Tool(B)
        // given A and B, find transformer T
        // apply transform on A -> B
        // invoke Tool(B) with B




        return List.of();
    }
}
