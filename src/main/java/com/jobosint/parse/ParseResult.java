package com.jobosint.parse;

import lombok.Data;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

@Data
public class ParseResult<T> {
    private final List<String> errors = new ArrayList<>();
    private T data;
    private HttpRequest priceRequest;
    public void addError(String msg) {
        this.errors.add(msg);
    }
}
