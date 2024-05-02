package com.jobosint.parse;

import com.jobosint.model.HtmlContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GenericHtmlFileParser implements HtmlFileParser<HtmlContent> {
    @Override
    public ParseResult<HtmlContent> parse(Path path) {
        ParseResult<HtmlContent> pr = new ParseResult<>();
        File input = path.toFile();
        if (!input.exists()) {
            pr.addError("File does not exist: " + path);
            return pr;
        }

        try {
            Document doc = Jsoup.parse(input, "UTF-8");
            pr.setData(new HtmlContent(doc));
        } catch (IOException e) {
            pr.addError("Error parsing file: " + path);
        }
        return pr;
    }

    @Override
    public ParseResult<HtmlContent> parse(String html) {
        Document doc = Jsoup.parse(html, "UTF-8");
        ParseResult<HtmlContent> pr = new ParseResult<>();
        pr.setData(new HtmlContent(doc));
        return pr;
    }

    @Override
    public ParseResult<HtmlContent> parse(String html, String selector) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ParseResult<HtmlContent> parse(Path path, String selector) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
