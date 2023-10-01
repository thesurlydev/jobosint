package com.jobosint.parser;

import com.jobosint.event.PersistPartEvent;
import com.jobosint.model.Part;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OeamPartsOnlineParserTest {

    @Autowired
    private OemPartsOnlinePageParser oemPartsOnlinePageParser;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void parseOemPartsOnlinePage() throws Exception {
        Path contentPath = Path.of("/home/shane/projects/jobosint/content/oempartsonline/body--bumper-and-components-front.html");
        ParseResult<List<Part>> result = oemPartsOnlinePageParser.parse(contentPath);
        List<Part> parts = result.getData();
        assertEquals(11, parts.size());
        for (Part part : parts) {
            System.out.println(part.toString());
            applicationEventPublisher.publishEvent(new PersistPartEvent(this, part));
        }

        // give time to persist to db
        Thread.sleep(1000);
    }
}
