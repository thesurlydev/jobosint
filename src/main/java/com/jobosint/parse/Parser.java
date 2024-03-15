package com.jobosint.parse;

import java.nio.file.Path;

public interface Parser<T> {
     ParseResult<T> parse(String html, String selector);
     ParseResult<T> parse(Path path, String selector);
}
