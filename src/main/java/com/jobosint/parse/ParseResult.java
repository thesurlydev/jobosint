package com.jobosint.parse;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParseResult<T> {
    private final List<String> errors = new ArrayList<>();
    private T data;
    public void addError(String msg) {
        this.errors.add(msg);
    }
}
