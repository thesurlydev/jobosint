package com.jobosint.parser;

import com.jobosint.model.Part;
import com.jobosint.model.VendorPart;
import com.jobosint.parse.OemPartsOnlinePageParser;
import com.jobosint.parse.ParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("parse")
public class OeamPartsOnlineParserTest {

    private OemPartsOnlinePageParser oemPartsOnlinePageParser;

    @BeforeEach
    public void setup() {
        oemPartsOnlinePageParser = new OemPartsOnlinePageParser();
    }

    @Test
    public void parseOemPartsOnlinePage() throws Exception {
        Path contentPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline/body--bumper-and-components-front.html");
        ParseResult<List<VendorPart>> result = oemPartsOnlinePageParser.parse(contentPath);
        List<VendorPart> vendorParts = result.getData();
        assertEquals(11, vendorParts.size());
        for (VendorPart vendorPart : vendorParts) {
            System.out.println(vendorPart.toString());
        }

        // give time to persist to db
        Thread.sleep(1000);
    }
}
