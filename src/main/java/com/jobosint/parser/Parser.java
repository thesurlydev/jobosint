package com.jobosint.parser;

import java.io.IOException;

public interface Parser<S, T> {
     ParseResult<T> parse(S input) throws IOException;
}
