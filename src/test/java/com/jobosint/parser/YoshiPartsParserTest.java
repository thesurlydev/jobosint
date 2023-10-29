package com.jobosint.parser;

import com.jobosint.model.Part;
import com.jobosint.parse.ParseResult;
import com.jobosint.parse.YoshiPartsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class YoshiPartsParserTest {
    private YoshiPartsParser parser;

    @BeforeEach
    public void setup() {
        parser = new YoshiPartsParser();
    }

    @Test
    public void parse() throws Exception {
        Path testPath = Path.of("/home/shane/projects/jobosint/content/yoshiparts/1074648_175.json");
        ParseResult<List<Part>> result = parser.parse(testPath);
        System.out.println(result);


    }
}
