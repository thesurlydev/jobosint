package com.jobosint.util;

import com.github.slugify.Slugify;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class StringUtils {

    public static String slugify(String in) {
        Slugify slugify = Slugify.builder().lowerCase(true).build();
        return slugify.slugify(in);
    }

    public static String removeQueryString(String urlString) {
        try {
            URI uri = URI.create(urlString);
            URL url = uri.toURL();
            return url.toString();
        } catch (MalformedURLException e) {
            // Handle exception if the URL is malformed or cannot be parsed
            e.printStackTrace();
            return null;
        }
    }
}
