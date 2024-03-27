package com.jobosint.parse;


import com.jobosint.model.JobDescription;
import com.jobosint.util.ConversionUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDescriptionParser implements Parser<JobDescription> {

    @Override
    public ParseResult<JobDescription> parse(String html, String selector) {
        Document doc = Jsoup.parse(html, "UTF-8");

        ParseResult<JobDescription> result = new ParseResult<>();

        // deal with parse errors
        ParseErrorList errors = doc.parser().getErrors();
        if (!errors.isEmpty()) errors.forEach(err -> {
            result.addError(err.getErrorMessage());
        });
        if (!result.getErrors().isEmpty()) {
            return result;
        }

        // transform to structured data
        Element head = doc.head();
        Element body = doc.body();
        String text = body.text();

        Elements article = body.select(selector);
        String markdown = ConversionUtils.convertToMarkdown(article.toString());

        JobDescription jd = new JobDescription();
        jd.setRawHead(head.toString());
        jd.setRawBody(body.toString());
        jd.setRawText(text);
        jd.setMarkdownBody(markdown);

        result.setData(jd);

        return result;
    }

    @Override
    public ParseResult<JobDescription> parse(Path path, String selector) {
        log.info("Parsing {}", path);

        ParseResult<JobDescription> parseResult = new ParseResult<>();

        File input = path.toFile();
        if (!input.exists()) {
            parseResult.addError("File does not exist: " + path);
            return parseResult;
        }

        String html;
        try {
            html = Files.readString(path);
        } catch (IOException e) {
            parseResult.addError("Error reading file: " + path);
            return parseResult;
        }

        return parse(html, selector);
    }
}
