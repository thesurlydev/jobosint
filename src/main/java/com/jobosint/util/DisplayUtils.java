package com.jobosint.util;

import java.util.StringJoiner;

public class DisplayUtils {

    // used by thymeleaf views
    @SuppressWarnings("unused")
    public static String salaryDisplay(String salaryMin, String salaryMax) {
        if ((salaryMin == null || salaryMin.isEmpty()) && (salaryMax == null || salaryMax.isBlank())) {
            return "n/a";
        } else if (salaryMin != null && !salaryMin.isBlank() && (salaryMax == null || salaryMax.isBlank())) {
            return salaryMin;
        } else {
            var range = new StringJoiner(" to ")
                    .add(salaryMin)
                    .add(salaryMax)
                    .toString();
            return "$" + range + "K";
        }
    }
}
