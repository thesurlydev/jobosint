package com.jobosint.model;

import org.junit.jupiter.api.Test;

import java.util.List;

public class YoshiPriceRequestTest {


    @Test
    public void toJson(){

        YoshiPriceRequest r = new YoshiPriceRequest(List.of("one"));
        String json = r.toJson();

        System.out.println(json);
    }
}