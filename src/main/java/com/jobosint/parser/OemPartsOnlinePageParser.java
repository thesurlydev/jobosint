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
        ParseResult<List<Part>> result = new ParseResult<>();
        Document doc;
        try {
            doc = Jsoup.parse(path.toFile(), "UTF-8");
        } catch (IOException e) {
            result.addError(e.getMessage());
            return result;
        }
        Elements partContainers = doc.getElementsByClass("part-group-container");
        if (partContainers.size() >= 2) {
            Element partContainer = partContainers.get(1);
            Elements partRows = partContainer.select("div.catalog-product");
            if (partRows.isEmpty()) {
                result.addError("No parts found");
                return result;
            }
            List<Part> parts = partRows.stream().map(row -> {
                String refCode = row.select("div.reference-code-col").text();
                String refImage = row.select("div.product-image-col img").attr("src");

                String title = row.select("strong.product-title").text();
                String partNum = row.select("div.product-partnum").text();
                String info = row.select("div.product-more-info").text();
                return new Part(null, partNum, title, info, path.toString(), refCode, refImage);
            }).toList();
            result.setData(parts);
        }
        return result;
    }
}
