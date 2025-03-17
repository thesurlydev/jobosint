package com.jobosint.model;

import com.jobosint.exception.InvalidScrapeRequestException;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Selector;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ScrapeRequest {
    private final String url;
    private final String selector;
    private final SelectAttribute attribute;
    private final String attributeValue;
    private final Set<FetchAttribute> fetch;
    private final List<Map<String, String>> cookies;

    public ScrapeRequest(String url,
                         String selector,
                         SelectAttribute attribute,
                         String attributeValue,
                         Set<FetchAttribute> fetch,
                         List<Map<String, String>> cookies) {
        this.url = url;
        this.selector = selector;
        this.attribute = attribute;
        this.attributeValue = attributeValue;
        this.fetch = fetch;
        this.cookies = cookies;
    }

    public ScrapeRequest(String url, List<Map<String, String>> cookies) {
        this(url, "html", SelectAttribute.html, null, Set.of(FetchAttribute.html), cookies);
    }

    public ScrapeRequest(String url) {
        this(url, "html", SelectAttribute.html, null, Set.of(FetchAttribute.html), null);
    }

    public ScrapeRequest(String url, FetchAttribute... attributes) {
        this(url, "html", SelectAttribute.html, null, Set.of(attributes), null);
    }

    public ScrapeRequest(String url, List<Map<String, String>> cookies, FetchAttribute... attributes) {
        this(url, "html", SelectAttribute.html, null, Set.of(attributes), cookies);
    }

    public ScrapeRequest(String url, String html, SelectAttribute selectAttribute, Object o, Set<FetchAttribute> attributes) {
        this(url, html, selectAttribute, null, attributes, null);
    }

    public boolean fetchHar() {
        return fetch != null && fetch.contains(FetchAttribute.har);
    }

    public boolean fetchText() {
        return fetch != null && fetch.contains(FetchAttribute.text);
    }

    public boolean fetchHtml() {
        return fetch != null && fetch.contains(FetchAttribute.html);
    }

    public boolean fetchPdf() {
        return fetch != null && fetch.contains(FetchAttribute.pdf);
    }

    public boolean fetchScreenshot() {
        return fetch != null && fetch.contains(FetchAttribute.screenshot);
    }

    public boolean isLinkData() {
        return attribute == SelectAttribute.attr && attributeValue.equals("href")
                || attribute == SelectAttribute.attr && attributeValue.equals("src");
    }

    public void validate() {
        if (this.attribute() == SelectAttribute.attr && this.attributeValue() == null) {
            throw new InvalidScrapeRequestException("If sel is 'attr', you must provide a value for 'attrVal'");
        }

        if (!isValidSelector(this.selector())) {
            throw new InvalidScrapeRequestException("Invalid value for 'sel': '" + this.selector() + "'");
        }

        // validate the url is valid
        URL validatedUrl = getValidatedUrl();

        // Check for the presence of a host
        if (validatedUrl.getHost() == null || validatedUrl.getHost().isEmpty()) {
            throw new InvalidScrapeRequestException("No host provided");
        }
    }

    @NotNull
    private URL getValidatedUrl() {
        URL validatedUrl;
        try {
            validatedUrl = new URI(this.url()).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidScrapeRequestException("Invalid value for 'url': '" + this.url() + "'");
        }

        // Check for HTTP or HTTPS protocols
        if (!"http".equals(validatedUrl.getProtocol()) && !"https".equals(validatedUrl.getProtocol())) {
            throw new InvalidScrapeRequestException("Invalid protocol");
        }
        return validatedUrl;
    }

    private boolean isValidSelector(String selector) {
        try {
            Parser.parse(selector, "");
            Selector.select(selector, new Element("root"));
            return true;
        } catch (Selector.SelectorParseException e) {
            return false;
        }
    }

    public List<Map<String, String>> cookies() { return cookies; }

    public String url() {
        return url;
    }

    public String selector() {
        return selector;
    }

    public SelectAttribute attribute() {
        return attribute;
    }

    public String attributeValue() {
        return attributeValue;
    }

    public Set<FetchAttribute> fetch() {
        return fetch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScrapeRequest) obj;
        return Objects.equals(this.url, that.url) &&
                Objects.equals(this.selector, that.selector) &&
                Objects.equals(this.attribute, that.attribute) &&
                Objects.equals(this.attributeValue, that.attributeValue) &&
                Objects.equals(this.cookies, that.cookies) &&
                Objects.equals(this.fetch, that.fetch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, selector, attribute, attributeValue, cookies, fetch);
    }

    @Override
    public String toString() {
        return "ScrapeRequest[" +
                "url=" + url + ", " +
                "selector=" + selector + ", " +
                "attribute=" + attribute + ", " +
                "attributeValue=" + attributeValue + ", " +
                "cookies=" + Strings.join(cookies,    ',') +
                "fetch=" + fetch + ']';
    }

}
