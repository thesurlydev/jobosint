package com.jobosint.parser;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jobosint.model.Part;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ToyotaPartsDealPageParser implements Parser<Path, List<Part>> {


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

        Element scriptEl = doc.getElementById("initialState");
        if (scriptEl == null) {
            String err = "No script with initialState found for " + path;
            log.error(err);
            result.addError(err);
            return result;
        }

        String rawJs = scriptEl.data();
        String json = rawJs.replace("window.__INITIAL_STORE__ = {", "{");
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

        List<List<Map<String, Object>>> list = JsonPath.read(document, "$.['partList'].['partList'][*]");

        List<Part> parts = new ArrayList<>();
        for (List<Map<String, Object>> arr : list) {
            Map<String, Object> map = arr.get(0);
            String partNumber = (String) map.get("partNumber");
            String description = (String) map.get("mainDesc");

            Map<String, String> priceInfo = (Map<String, String>) map.get("priceInfo");
            String msrp = priceInfo.get("retail");
            String price = priceInfo.get("price");

            Part p = new Part(null, partNumber, description, null, path.toString(), null, null, null, null, null, msrp, price, "toyotapartsdeal", "Toyota", partNumber);
            parts.add(p);
        }
        result.setData(parts);

        return result;
    }
}
