package com.jobosint.util;

import com.github.slugify.Slugify;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class StringUtils {

    public static String slugify(String in) {
        Slugify slugify = Slugify.builder().lowerCase(true).build();
        return slugify.slugify(in);
    }

    public static String removeQueryString(String urlString) {
        try {
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), null, null);
            return uri.toString();
        } catch (MalformedURLException | URISyntaxException e) {
            // Handle exception if the URL is malformed or cannot be parsed
            e.printStackTrace();
            return null;
        }
    }
}
