package com.jobosint.service;

import com.jobosint.model.DownloadContentRequest;
import com.jobosint.model.DownloadRequest;
import com.jobosint.model.Part;
import com.jobosint.model.VendorPart;
import com.jobosint.parse.ParseResult;
import com.jobosint.parse.YoshiPartsParser;
import com.jobosint.util.FileUtils;
import org.jsoup.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootTest
public class DownloadServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private YoshiPartsParser yoshiPartsParser;


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
        downloadService.downloadAll(HttpMethod.GET, linksPath, targetDir, false, ".html");
    }

    @Test
    public void downloadCruiserYardPagesTest() throws Exception {
        String namespace = "cruiseryard";
        Path linksPath = Paths.get("content", namespace + "-links.txt");
        String targetDir = "/home/shane/projects/jobosint/content/" + namespace;
        downloadService.downloadAll(HttpMethod.GET, linksPath, targetDir, false, ".html");
    }

    @Test
    public void downloadYoshiPriceTest() throws Exception {
        Path targetDir = Path.of("/home/shane/projects/jobosint/content/yoshiparts");
        Path testPath = targetDir.resolve("1074648_175.json");
        ParseResult<List<Part>> result = yoshiPartsParser.parse(testPath);
        DownloadRequest dr = new DownloadRequest(result.getPriceRequest(), targetDir, "1074648_175-prices.json", true);
        downloadService.download(dr);
    }

    @Test
    public void downloadAllYoshiPricesTest() throws Exception {
        Path targetDir = Path.of("/home/shane/projects/jobosint/content/yoshiparts");
        try (Stream<Path> stream = Files.list(targetDir)) {
            stream.forEach(path -> {
                ParseResult<List<Part>> result = yoshiPartsParser.parse(path);
                Path filename = path.getFileName();
                String priceFilename = filename.toString().replace(".json", "-prices.json");
                System.out.println(priceFilename);
                DownloadRequest dr = new DownloadRequest(result.getPriceRequest(), targetDir, priceFilename, true);
                try {
                    Optional<Path> localPath = downloadService.download(dr);
                    localPath.ifPresent(value -> System.out.println("Downloaded: " + value));
                    Thread.sleep(500);
                } catch (InterruptedException | URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    public void downloadYoshiPartsTest() throws Exception {
        String namespace = "yoshiparts";
        Path linksPath = Paths.get("content", namespace + "-links.txt");
        String targetDir = "/home/shane/projects/jobosint/content/" + namespace;

        // parse uid's from each downloaded JSON ("uid": "toyota-8522090A02")
        // create a request to get the prices
        /*
        curl 'https://api.yoshiparts.com/flat-offers' \
          -H 'accept: application/json' \
          -H 'content-type: application/json' \
          --data-raw '{"productUids":["toyota-0431760020","toyota-1130261030","toyota-1130261031","toyota-1131161020","toyota-1131261020","toyota-1131261021","toyota-1132861010","toyota-1136960011","toyota-1139160010","toyota-3110460020","toyota-3111160150","toyota-3113160010","toyota-3113260020","toyota-3113360020","toyota-3113460020","toyota-3113560020","toyota-3113660010","toyota-3113760010","toyota-4679137150","toyota-9011910041","toyota-9014910001","toyota-9017906158","toyota-9031145008","toyota-9095001200","toyota-9095001580","toyota-9121251232","toyota-9161160814","toyota-9161161020","toyota-91612B1020","toyota-9161560816","toyota-9165140610","toyota-9338116008","toyota-9338116016","toyota-9338126014","toyota-9451201200"]}' \
          --compressed
         */
        // save the prices to a file

        downloadService.downloadAll(HttpMethod.POST, linksPath, targetDir, false, ".json");
    }

    @Test
    public void downloadAllImagesTest() throws Exception {
        String namespace = "yoshiparts";
        Path linksPath = Paths.get("/home/shane/projects/jobosint/images", namespace + "-image-links.txt");
        List<String> urls = Files.readAllLines(linksPath);
        Path targetDir = Paths.get("/home/shane/projects/jobosint/images", namespace);
        downloadService.downloadAllImages(urls, targetDir);
    }

}
