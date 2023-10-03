package com.jobosint.parser;

public interface Parser<S, T> {
     ParseResult<T> parse(S input);
}
