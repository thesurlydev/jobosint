package com.jobosint.util;

import java.util.StringJoiner;

public class DisplayUtils {

    // used by thymeleaf views
    @SuppressWarnings("unused")
    public static String salaryDisplay(String salaryMin, String salaryMax) {
        if ((salaryMin == null || salaryMin.isEmpty()) && (salaryMax == null || salaryMax.isBlank())) {
            return "n/a";
        } else if (salaryMin != null && !salaryMin.isBlank() && (salaryMax == null || salaryMax.isBlank())) {
            return new StringJoiner(" to ")
                    .add("$" + salaryMin + "K")
                    .add("?")
                    .toString();
        } else {
            return new StringJoiner(" to ")
                    .add("$" + salaryMin + "K")
                    .add("$" + salaryMax + "K")
                    .toString();
        }
    }
}
