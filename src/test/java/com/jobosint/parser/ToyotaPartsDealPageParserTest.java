package com.jobosint.parser;

import com.jobosint.model.VendorPart;
import com.jobosint.parse.ParseResult;
import com.jobosint.parse.ToyotaPartsDealPageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

@Tag("parse")
public class ToyotaPartsDealPageParserTest {
    private ToyotaPartsDealPageParser parser;

    @BeforeEach
    public void setup() {
        parser = new ToyotaPartsDealPageParser();
    }

    @Test
    public void parse() throws Exception {
        Path path = Path.of("/home/shane/projects/jobosint/content/toyotapartsdeal/water_pump.html");
        ParseResult<List<VendorPart>> result = parser.parse(path);
    }
}
