package com.jobosint.parser;

import com.jobosint.model.Part;
import com.jobosint.parse.CruiserCorpsProductParser;
import com.jobosint.parse.ParseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@Tag("parse")
public class CruiserCorpsProductParserTest {
    private CruiserCorpsProductParser parser;

    @BeforeEach
    public void setup() {
        parser = new CruiserCorpsProductParser();
    }

    @Test
    public void parse() throws Exception {
        Path testPath = Path.of("/home/shane/projects/jobosint/content/cruisercorps/2f-engine-overhaul-gasket-kit-oem.json");
        ParseResult<Part> result = parser.parse(testPath);
        System.out.println(result);
        Assertions.assertEquals("2F Engine Overhaul Gasket Kit - OEM - FJ40, FJ45, FJ60 1980-1987", result.getData().name());
    }
}
