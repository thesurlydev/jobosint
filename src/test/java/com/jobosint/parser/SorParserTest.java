package com.jobosint.parser;

import com.jobosint.model.Part;
import com.jobosint.parse.ParseResult;
import com.jobosint.parse.SorParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("parse")
public class SorParserTest {

    private SorParser sorParser;

    @BeforeEach
    public void setup() {
        sorParser = new SorParser();
    }

    @Test
    public void lookupCategory() {
        String cat = sorParser.lookupCategory("106b");
        System.out.println(cat);
        assertEquals("Bumpers - Factory & Bumper Ends, Frames, Skidplates", cat);
    }

    @Test
    public void parse() throws Exception {
        Path path = Path.of("/home/shane/projects/jobosint/content/sor/cat106B.sor");
        ParseResult<List<Part>> result = sorParser.parse(path);

    }
}
