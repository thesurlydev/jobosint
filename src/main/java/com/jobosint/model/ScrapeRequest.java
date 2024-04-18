package com.jobosint.model;

import com.jobosint.exception.InvalidScrapeRequestException;
import org.jetbrains.annotations.NotNull;
import org.jsoup.parser.Parser;
import org.jsoup.select.Selector;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

public record ScrapeRequest(String url,
                            String selector,
                            SelectAttribute attribute,
                            String attributeValue,
                            Set<FetchAttribute> fetch) {
    public boolean fetchHar() {
        return fetch != null && fetch.contains(FetchAttribute.har);
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
            Selector.select(selector, new org.jsoup.nodes.Element("root"));
            return true;
        } catch (Selector.SelectorParseException e) {
            return false;
        }
    }
}
