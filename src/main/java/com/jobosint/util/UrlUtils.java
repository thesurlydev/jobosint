package com.jobosint.util;

import com.github.slugify.Slugify;

import java.net.*;
import java.nio.charset.StandardCharsets;

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

    public static String host(String url) {
        try {
            return new URI(url).getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String removeQueryString(String url) {
        var nurl = url;
        if (nurl.contains("?")) {
            nurl = url.substring(0, url.indexOf("?"));
        }
        return nurl;
    }

    public static String slugify(String in) {
        Slugify slugify = Slugify.builder().lowerCase(true).build();
        return slugify.slugify(in);
    }

    public static String decodeContent(String data) {
        if (data == null) return null; // Early return for null input

        StringBuilder tempBuffer = new StringBuilder();
        try {
            for (char c : data.toCharArray()) {
                switch (c) {
                    case '%':
                        tempBuffer.append("<percentage>"); // Placeholder for '%'
                        break;
                    case '+':
                        tempBuffer.append("<plus>"); // Placeholder for '+'
                        break;
                    default:
                        tempBuffer.append(c);
                }
            }
            String decodedData = URLDecoder.decode(tempBuffer.toString(), StandardCharsets.UTF_8);
            return decodedData
                    .replaceAll("<percentage>", "%")
                    .replaceAll("<plus>", "+");
        } catch (Exception e) {
            return null; // Return null or some error indication as per your error handling policy
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
