package com.jobosint.util;

import com.github.slugify.Slugify;

public class StringUtils {
    public static String slugify(String in) {
        Slugify slugify = Slugify.builder().lowerCase(true).build();
        return slugify.slugify(in);
    }
}
