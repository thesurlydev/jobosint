package com.jobosint.model;

import org.jsoup.nodes.Element;

public enum SelectAttribute {
    attr,
    html,
    text;

    public String select(Element el, String attrVal) {
        return switch (this) {
            case attr -> el.attr(attrVal);
            case html -> el.html();
            case text -> el.text();
        };
    }
}
