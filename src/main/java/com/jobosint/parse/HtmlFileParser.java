package com.jobosint.parse;

import java.nio.file.Path;

public interface HtmlFileParser<T> extends HtmlParser<T> {
     ParseResult<T> parse(Path path, String selector);
}
