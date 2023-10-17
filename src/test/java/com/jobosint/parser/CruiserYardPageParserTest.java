package com.jobosint.parser;

import com.jobosint.parse.CruiserYardPageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@Tag("parse")
public class CruiserYardPageParserTest {

    private CruiserYardPageParser parser;

    @BeforeEach
    public void setup() {
        parser = new CruiserYardPageParser();
    }

    @Test
    public void parse() {


        Path path = Path.of("/home/shane/projects/jobosint/content/cruiseryard/page-1.html");

        parser.parse(path);

    }
}
