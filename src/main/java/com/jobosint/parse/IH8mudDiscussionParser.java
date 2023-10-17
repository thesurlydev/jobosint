package com.jobosint.parse;

import com.jobosint.model.ParseRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Given a discussion get a list of the first page of all the threads within the discussion.
 */
@Component
@Slf4j
public class IH8mudDiscussionParser implements Parser<ParseRequest, List<String>> {

    @Override
    public ParseResult<List<String>> parse(ParseRequest parseRequest) {
        ParseResult<List<String>> result = new ParseResult<>();
        java.nio.file.Path path = parseRequest.path();

        if (path == null) {
            result.addError("Path required");
            return result;
        }

        if (!path.toFile().exists()) {
            result.addError("File not found: " + path);
            return result;
        }
        log.info("Parsing: {}", path);

        Document doc;
        try {
            doc = Jsoup.parse(path.toFile(), "UTF-8");
        } catch (IOException e) {
            String err = "Error parsing: " + path + "; " + e.getMessage();
            log.error(err);
            result.addError(err);
            return result;
        }
        /*
        containerElements
         -> parentElements
           -> tagElements
             -> attrs
               -> containing
               -> excluding
               -> distinct
               -> sort
         */

        Elements containerElements;
        if (parseRequest.containerClass() == null) {
            containerElements = doc.body().children();
        } else {
            containerElements = doc.getElementsByClass(parseRequest.containerClass());
        }

        Elements parents;
        if (parseRequest.parentContainerQuery() == null) {
            parents = containerElements;
        } else {
            parents = containerElements.get(0).select(parseRequest.parentContainerQuery());
        }

        List<String> data = parents.stream().flatMap(p -> {
            Elements tagElements = p.getElementsByTag(parseRequest.tag());
            return tagElements.stream()
                    .map(el -> el.attr("href"))
                    .filter(href -> parseRequest.containing().stream().allMatch(href::contains))
                    .filter(href -> parseRequest.excluding().stream().noneMatch(href::contains))
                    .distinct()
                    .map(href -> {
                        if (parseRequest.baseUrl() != null) {
                            return parseRequest.baseUrl() + href;
                        } else {
                            return href;
                        }
                    })
                    .sorted();
        }).toList();

        result.setData(data);

        return result;
    }

}
