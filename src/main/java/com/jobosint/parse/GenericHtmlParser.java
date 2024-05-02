package com.jobosint.parse;

import com.jobosint.model.HtmlContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GenericHtmlParser implements HtmlParser<HtmlContent> {
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
}
