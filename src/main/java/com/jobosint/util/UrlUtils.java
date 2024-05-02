package com.jobosint.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtils {

    public static boolean isValid(String url) {
        try {
            new URI(url).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    public static String getBaseUrl(String s) {
        String baseUrl;
        try {
            URI uri = new URI(s);
            URL url = uri.toURL();
            int port = url.getPort();
            if (port == -1) {
                baseUrl = url.getProtocol() + "://" + url.getHost();
            } else {
                baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + port;
            }
        } catch (URISyntaxException | MalformedURLException e) {
            return null;
        }
        return baseUrl;
    }

    public static String slugify(String url) {
        if (url == null) {
            return "";
        }
        return url.replaceFirst("https://", "").replaceAll("[^a-zA-Z0-9]", "_");
    }

    public static String host(String url) {
        try {
            return new URI(url).getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String apexDomain(String url) {
        String host = host(url);
        if (host == null) {
            return null;
        }

        String[] parts = host.split("\\.");
        if (parts.length < 2) {
            return host;
        }

        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }
}
