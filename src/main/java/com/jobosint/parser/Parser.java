package com.jobosint.parser;

import java.io.IOException;

public interface Parser<T> {
     public T parse(String path) throws IOException;
}
