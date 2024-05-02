package com.jobosint.parse;

public interface HtmlParser<T> {
    ParseResult<T> parse(String html);
    ParseResult<T> parse(String html, String selector);
}
