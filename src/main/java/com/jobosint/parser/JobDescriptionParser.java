package com.jobosint.parser;


import com.jobosint.model.JobDescription;
import com.jobosint.model.JobDescriptionParseResult;
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

@Component
@Slf4j
public class JobDescriptionParser implements Parser<JobDescriptionParseResult> {
    @Override
    public JobDescriptionParseResult parse(String path) throws IOException {
        log.info("parsing!");

        JobDescriptionParseResult result = new JobDescriptionParseResult();

        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");
        doc.select("script").remove();

        ParseErrorList errors = doc.parser().getErrors();
        result.setErrorCount(errors.size());
        log.info("Found {} parsing errors", errors.size());
        if (!errors.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errors.forEach(err -> {
                errorMessages.add(err.getErrorMessage());
            });
            result.setErrorMessages(Optional.of(errorMessages));
            return result;
        }

        // no errors; continue
        JobDescription jd = new JobDescription();

        logTags(doc, "meta");

        Optional<String> maybeOgTitle = getMetaValue(doc, "og:title");
        maybeOgTitle.ifPresent(jd::setTitle);


        Element body = doc.body();
        log.info("body: {}", body.text());

        result.setJobDescription(Optional.of(jd));

        return result;
    }

    private void logTags(Document doc, String tagName) {
        Elements metaElements = doc.getElementsByTag(tagName);
        for(Element metaEl : metaElements) {
            log.info(metaEl.toString());
        }
    }

    private Optional<String> getMetaValue(Document doc, String key) {
        Elements metaElements = doc.getElementsByTag("meta");
        for(Element metaEl : metaElements) {
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
