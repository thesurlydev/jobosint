package com.jobosint.service;

import com.jobosint.service.DownloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DownloadServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Test
    public void downloadAllTest() throws Exception {
        Path linksPath = Path.of("oempartsonline-links.txt");
        List<Optional<Path>> localPaths = downloadService.downloadAll(linksPath);
        /*int totalParts = 0;
        for (Optional<Path> maybePath : localPaths) {
            if (maybePath.isPresent()) {
                Path path = maybePath.get();
                ParseResult<List<Part>> result = oemPartsOnlinePageParser.parse(path);
                if (result.ok()) {
                    List<Part> parts = result.getData();
                    int numParts = parts.size();
                    totalParts += numParts;
                    System.out.println("Found " + numParts + " for " + path);
                    for (Part part : parts) {
                        System.out.println(part);
                        downloadService.downloadPartSouqImage(part.num());
                    }
                }
            }
        }
        System.out.println("total: " + totalParts);*/
    }

    /*@Test
    public void downloadImage() throws Exception {
        downloader.downloadPartSouqImage("21912-61035");
    }*/


}
