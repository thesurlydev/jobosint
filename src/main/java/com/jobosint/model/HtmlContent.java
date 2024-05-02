package com.jobosint.model;

import org.jsoup.nodes.Document;

public record HtmlContent(Document document) {

    public String text() {
        return document.text();
    }

    public String location() {
        return document.location();
    }

    public String title() {
        return document.title();
    }

    public String html() {
        return document.html();
    }

}
