package com.jobosint.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ParseUtils {

    public static Document getDocumentFromPath(Path path) throws IOException {
        log.info("Parsing: {}", path);
        return Jsoup.parse(path.toFile(), "UTF-8");
    }

    public static String[] parseSalaryRange(String description) {
        // Define the regex pattern to match salary information
        String regexPattern = "\\$?\\d{1,3}(,\\d{3})*(\\.\\d{2})?";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(description);

        // Find and store unique numerical salary values within the specified range
        List<Double> salaries = new ArrayList<>();
        while (matcher.find()) {
            String salaryString = matcher.group().replaceAll("[\\$,]", "");
            double salaryValue = Double.parseDouble(salaryString);
            if (salaryValue >= 30000 && salaryValue <= 999000) {
                salaries.add(salaryValue);
            }
        }

        // Convert salaries to "XXX.X" format
        List<String> convertedSalaries = new ArrayList<>();
        for (double salary : salaries) {
            double convertedSalary = salary / 1000.0;
            convertedSalaries.add(String.format("%.1f", convertedSalary));
        }

        if (convertedSalaries.size() > 2) {
            convertedSalaries = convertedSalaries.subList(0, 2);
        }

        return convertedSalaries.toArray(new String[0]);
    }

}
