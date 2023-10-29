package com.jobosint.parse;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jobosint.model.Manufacturer;
import com.jobosint.model.Part;
import com.jobosint.model.YoshiPriceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jobosint.model.PartClassification.OEM;

@Slf4j
@Component
public class YoshiPartsParser implements Parser<Path, List<Part>> {
    @Override
    public ParseResult<List<Part>> parse(Path path) {
        log.info("Parsing: {}", path);
        ParseResult<List<Part>> result = new ParseResult<>();

        String json;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

        List<Map<String, Object>> products = JsonPath.read(document, "$.products");
        List<String> uids = new ArrayList<>();
        List<Part> parts = products.stream().map(map -> {
                    String partNumber = (String) map.get("numberFormatted");
                    String name = (String) map.get("name");

                    String uid = (String) map.get("uid");
                    uids.add(uid);

                    return new Part(null, partNumber, name, null, Manufacturer.TOYOTA.id(), null, null, null, OEM);
                }).peek(System.out::println)
                .toList();

        result.setData(parts);

        HttpRequest priceRequest = new YoshiPriceRequest(uids).httpRequest();
        result.setPriceRequest(priceRequest);

        return result;
    }
}
