package com.jobosint.parser;

import com.jobosint.model.Part;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class OemPartsOnlinePageParser implements Parser<Path, List<Part>> {
    @Override
    public ParseResult<List<Part>> parse(Path path) {
        log.info("Parsing: {}", path);
        ParseResult<List<Part>> result = new ParseResult<>();
        Document doc;
        try {
            doc = Jsoup.parse(path.toFile(), "UTF-8");
        } catch (IOException e) {
            String err = "Error parsing: " + path + "; " + e.getMessage();
            log.error(err);
            result.addError(err);
            return result;
        }

        // Body
        Elements categoryEls = doc.getElementsByClass("category-group")
                .select("a.category")
                .select("[aria-expanded=true]");

        String category;
        if (!categoryEls.isEmpty()) {
            category = categoryEls.get(0).attr("title");
        } else {
            category = null;
            log.warn("No category found for {}", path);
        }

        // Bumper & Components - Front
        Elements subcategoryEls = doc.getElementsByClass("category-group")
                .select("a.subcategory")
                .select("a.active-cat");
        String subcategory;
        if (!subcategoryEls.isEmpty()) {
            subcategory = subcategoryEls.get(0).text();
        } else {
            subcategory = null;
            log.warn("No subcategory found for {}", path);
        }


        Elements partContainers = doc.getElementsByClass("part-group-container");

        /*
        int numContainers = partContainers.size();
        if (numContainers >= 2) {
            Element partContainer = partContainers.get(1);
            parsePartContainer(partContainer, path, result);
        } else if (numContainers == 1) {
            Element partContainer = partContainers.get(0);
            parsePartContainer(partContainer, path, result);
        }
        */

        if (!partContainers.isEmpty()) {
            partContainers.forEach(pc -> parsePartContainer(pc, path, result, category, subcategory));
        }

        return result;
    }

    private void parsePartContainer(Element partContainer, Path path, ParseResult<List<Part>> result, String category, String subcategory) {
        Elements partRows = partContainer.select("div.catalog-product");
        if (partRows.isEmpty()) {
            String err = "No parts found for: " + path;
            log.error(err);
            result.addError(err);
            return;
        }
        List<Part> parts = partRows.stream().map(row -> {
            String refCode = row.select("div.reference-code-col").text();
            String refImage = row.select("div.product-image-col img").attr("src");

            String title = row.select("strong.product-title").text();
            String partNum = row.select("div.product-partnum").text();
            String info = row.select("div.product-more-info").text();
            String msrp = null;
            Elements msrpEls = row.select("div.product-pricing");
            if (!msrpEls.isEmpty()) {
                String raw = msrpEls.select("div.list-price").text();
                msrp = raw.replace("MSRP", "").trim();
            }
            String hash = Part.calcHash(partNum, title, info);
            return new Part(null, partNum, title, info, path.toString(), refCode, refImage, hash, category, subcategory, msrp);
        }).toList();
        result.setData(parts);
    }
}
