package com.jobosint.service;

import com.jobosint.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DownloadServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Test
    public void downloadAllFromSiteMapTest() throws Exception {
        String namespace = "coolcruisers";
        Path linksPath = Paths.get("content", "coolcruisers.com_sitemap.xml");
        String targetDir = "/home/shane/projects/jobosint/content/" + namespace;
        List<Optional<Path>> localPaths = downloadService.downloadAllFromSiteMap(linksPath, targetDir, false, null);
        for (Optional<Path> mp : localPaths) {
            mp.ifPresent(System.out::println);
        }
    }

    @Test
    public void downloadIh8mudDiscussionThreadsTest() throws Exception {
        String namespace = "ih8mud-60-series-tech-discussion-thread";
        Path linksPath = Paths.get("content", namespace + "-links.txt");
        String targetDir = "/home/shane/projects/jobosint/content/" + namespace;
        downloadService.downloadAll(linksPath, targetDir, false, ".html");
    }

    @Test
    public void downloadCruiserYardPagesTest() throws Exception {
        String namespace = "cruiseryard";
        Path linksPath = Paths.get("content", namespace + "-links.txt");
        String targetDir = "/home/shane/projects/jobosint/content/" + namespace;
        downloadService.downloadAll(linksPath, targetDir, false, ".html");
    }

}
