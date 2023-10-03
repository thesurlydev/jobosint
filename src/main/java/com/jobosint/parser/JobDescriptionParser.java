package com.jobosint.parser;


import com.jobosint.model.JobDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionParser implements Parser<String, JobDescription> {

    @Override
    public ParseResult<JobDescription> parse(String path) {
        log.info("Parsing {}", path);


        File input = new File(path);
        // TODO verify file exists
        Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Document size (init): {}", docSize(doc));

        ParseResult<JobDescription> result = new ParseResult<>();

        // deal with parse errors
        ParseErrorList errors = doc.parser().getErrors();
        if (!errors.isEmpty()) errors.forEach(err -> {
            result.addError(err.getErrorMessage());
        });

        // no errors; continue

        // remove useless tags
        Set<String> tagsToRemove = Set.of("form", "script", "style", "svg", "img");
        Document finalDoc = doc;
        tagsToRemove.forEach(tag -> {
            Elements removedElements = finalDoc.select(tag).remove();
            log.info("Removed {} '{}' tags", removedElements.size(), tag);
        });

        // TODO remove navigational tags/content
        // TODO remove empty tags

        log.info("Document size (post): {}", docSize(doc));

        // transform to structured data

        Element head = doc.head();
        Element body = doc.body();
        String text = doc.text();

        JobDescription jd = new JobDescription();
        jd.setRawHead(head.toString());
        jd.setRawBody(body.toString());
        jd.setRawText(text);

//        logTagAttributes(doc, "a", "href");
//        logTags(doc, "meta");

        System.out.println();
        List<String> links = getLinks(doc, true, true);
        for (String link : links) {
            System.out.println(link);
        }

        Optional<String> maybeOgTitle = getMetaValue(doc, "og:title");
        maybeOgTitle.ifPresent(jd::setTitle);

        result.setData(jd);

        return result;
    }

    private long docSize(Document doc) {
        return doc.toString().length();
    }

    private void removeEmptyTags(Document doc) {
        // TODO

    }

    private void removeElementsByTag(Document doc, String tag) {
        // TODO
    }

    private void removeElementsByTagAndAttrContaining(Document doc, String tag, String attrContains) {
        // TODO
    }

    private List<String> getLinks(Document doc, boolean excludeNonHttp, boolean excludeSocial) {

        Elements metaElements = doc.getElementsByTag("a");
        if (metaElements.isEmpty()) {
            return List.of();
        }

        List<String> socials = List.of("facebook", "twitter", "tiktok", "linkedin", "youtube", "instagram");
        List<String> links = new ArrayList<>();
        for (Element metaEl : metaElements) {
            String href = metaEl.attr("href");
            if (excludeNonHttp && !href.startsWith("http")) {
                continue;
            }
            if (excludeSocial && socials.stream().anyMatch(href::contains)) {
                continue;
            }
            links.add(href);
        }
        return links;
    }

    private void logTags(Document doc, String tagName) {
        Elements metaElements = doc.getElementsByTag(tagName);
        for (Element metaEl : metaElements) {
            log.info(metaEl.toString());
        }
    }

    private void logTagAttributes(Document doc, String tagName, String attr) {
        Elements metaElements = doc.getElementsByTag(tagName);
        for (Element metaEl : metaElements) {
            log.info(metaEl.attr(attr));
        }
    }

    private Optional<String> getMetaValue(Document doc, String key) {
        Elements metaElements = doc.getElementsByTag("meta");
        for (Element metaEl : metaElements) {
            String name = metaEl.attr("name");
            String property = metaEl.attr("property");
            String content = metaEl.attr("content");
            if (name.equals(key) || property.equals(key)) {
                return Optional.of(content);
            }
        }
        return Optional.empty();
    }
}
