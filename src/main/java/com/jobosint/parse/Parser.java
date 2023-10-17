package com.jobosint.parse;

public interface Parser<S, T> {
     ParseResult<T> parse(S input);
}
