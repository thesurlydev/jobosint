package com.jobosint.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class ParseUtils {

    public static Document getDocumentFromPath(Path path) throws IOException {
        log.info("Parsing: {}", path);
        return Jsoup.parse(path.toFile(), "UTF-8");
    }
}
