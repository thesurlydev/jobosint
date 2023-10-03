package com.jobosint.parser;

import com.jobosint.model.Part;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SorParser implements Parser<Path, List<Part>> {

    private Map<String, String> catLookupMap;

    public SorParser() {
        Path path = Path.of("/home/shane/projects/jobosint/content/sor-numeric-catalog-listing.html");
        this.catLookupMap = parseNumericCatalogListing(path);
    }


    @Override
    public ParseResult<List<Part>> parse(Path input) {

        // TODO h1 will be subcategory

        return null;
    }


    /**
     * Given a SOR catalog number, return the category name
     *
     * @param num
     * @return
     */
    public String lookupCategory(String num) {
        // it's possible something like 106b is passed. in that case, get the first 3 digits
        if (num.length() > 3) {
            num = num.substring(0, 3);
        }
        log.info("Looking up category for: {}", num);

        return this.catLookupMap.get(num);
    }

    private static Map<String, String> parseNumericCatalogListing(Path path) {
        log.info("Parsing: {}", path);

        Document doc;
        try {
            doc = Jsoup.parse(path.toFile(), "UTF-8");
        } catch (IOException e) {
            String err = "Error parsing: " + path + "; " + e.getMessage();
            log.error(err);
            return Map.of();
        }

        Elements links = doc.getElementsByClass("pbContainer").select("a");
        Map<String, String> map = new HashMap<>();
        links.forEach(link -> {
            String title = link.attr("title");
//            log.info("title: {}", title);
            String delim = "–"; // not a dash
            if (title.contains(delim)) {
                int dashIdx = title.indexOf(delim);
                String num = title.substring(0, dashIdx).trim();
                String cat = title.substring(dashIdx + 2).trim();

                if (map.containsKey(num)) {
                    String existCat = map.get(num);
                    cat = existCat + ", " + cat;
                }
                map.put(num, cat);

            } else {
                log.warn("Unexpected title: {}", title);
            }
        });
        return map;

    }
}

