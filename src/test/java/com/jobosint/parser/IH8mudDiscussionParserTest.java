package com.jobosint.parser;

import com.jobosint.model.ParseRequest;
import com.jobosint.parse.IH8mudDiscussionParser;
import com.jobosint.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Tag("parse")
public class IH8mudDiscussionParserTest {

    private IH8mudDiscussionParser ih8mudDiscussionParser;

    @BeforeEach
    public void setup() {
        ih8mudDiscussionParser = new IH8mudDiscussionParser();
    }


    @Test
    public void ih8mudForumThreadPagedLinks() {
        String threadPagesLinkFile = "/home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussion-thread-paged-links.txt";

        List<java.nio.file.Path> threadFilePaths = Arrays.stream(new File("/home/shane/projects/jobosint/content/test-threads").listFiles())
                .map(File::toPath)
                .sorted()
                .peek(System.out::println)
                .toList();

        threadFilePaths.stream()
                .map(path -> new ParseRequest(
                        path,
                        "https://forum.ih8mud.com",
                        "pageNav-main",
                        "li.pageNav-page",
                        "a",
                        null,
                        List.of("/threads/", "/page-"),
                        List.of(),
                        "href",
                        false))
                .flatMap(path -> {
                    System.out.println(path);
                    return ih8mudDiscussionParser.parse(path).getData().stream();
                })
                .peek(System.out::println)
                .map(link -> {
                    int pos = link.lastIndexOf("-");
                    return link.substring(pos + 1);
                })
                .mapToInt(Integer::parseInt)
                .max()
                .stream()
                .flatMap(max -> IntStream.rangeClosed(2, max))
                .mapToObj(pageNum -> "page-" + pageNum)
                .forEach(System.out::println);

    }

    /*private int findMaxPage(String path) {

    }*/

    @Test
    public void ih8mudForumThreadLinks() {

        String threadLinkFile = "/home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussion-thread-links.txt";

        // list all files in /home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussions
        List<java.nio.file.Path> discussionFilePaths = Arrays.stream(new File("/home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussions").listFiles())
                .map(File::toPath)
                .sorted()
//                .peek(System.out::println)
                .toList();

        // map to ParseRequest
        // flatMap to thread links
        // append threadLinks to file: /home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussion-thread-links.txt
        discussionFilePaths.stream()
                .flatMap(path -> {
                    ParseRequest parseRequest = new ParseRequest(
                            path,
                            "https://forum.ih8mud.com",
                            "p-body-content",
                            "div.structItem-name",
                            "a",
                            null,
                            List.of("/threads/"),
                            List.of("/latest", "/who-replied"),
                            "href",
                            false);
                    return ih8mudDiscussionParser.parse(parseRequest).getData().stream();
                })
                .map(link -> {
                    if (link.endsWith("/")) {
                        return link.substring(0, link.length() - 1);
                    } else {
                        return link;
                    }
                })
                .forEach(link -> {
                    FileUtils.appendToFile(threadLinkFile, link);
                });

        // TODO download all thread links to /home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussion-threads


        // TODO parse each thread page to get additional page links, if any
        // TODO download all thread page links


        /*ParseRequest parseRequest = new ParseRequest(
                Path.of("/home/shane/projects/jobosint/content/ih8mud-60-series-tech-discussions/page-2.html"),
                "https://forum.ih8mud.com",
                "p-body-content",
                "div.structItem-name",
                "a",
                null,
                "/threads/",
                List.of("/latest", "/who-replied"),
                "href",
                false);

        ParseResult<List<String>> result = ih8mudDiscussionParser.parse(parseRequest);
        System.out.println(result);*/
    }
}
