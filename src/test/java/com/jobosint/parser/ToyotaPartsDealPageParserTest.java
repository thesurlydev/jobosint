package com.jobosint.parser;

import com.jobosint.model.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToyotaPartsDealPageParserTest {
    private ToyotaPartsDealPageParser parser;

    @BeforeEach
    public void setup() {
        parser = new ToyotaPartsDealPageParser();
    }

    @Test
    public void parse() throws Exception {
        Path path = Path.of("/home/shane/projects/jobosint/content/toyotapartsdeal/water_pump.html");
        ParseResult<List<Part>> result = parser.parse(path);

    }
}
