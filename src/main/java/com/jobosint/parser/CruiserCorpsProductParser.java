package com.jobosint.parser;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jobosint.model.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class CruiserCorpsProductParser implements Parser<Path, Part> {
    @Override
    public ParseResult<Part> parse(Path path) {

        log.info("Parsing: {}", path);
        ParseResult<Part> result = new ParseResult<>();

        String json;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

        String title = JsonPath.read(document, "$.product.title");
        String category = JsonPath.read(document, "$.product.product_type");
        String refImage = null;
        try {
            refImage = JsonPath.read(document, "$.product.image.src");
        } catch (Exception e) {
            log.warn("No image available for {}", path);
        }
        Long partHashAsLong = JsonPath.read(document, "$.product.id");
        String partHash = partHashAsLong.toString();
        String info = JsonPath.read(document, "$.product.body_html");
        String source = path.toString();

        // TODO: this is CruserCorps SKU, not Toyota part number
        String num = JsonPath.read(document, "$.product.variants[0].sku");

        // TODO: this is CruiserCorps price, but using it as MSRP for now
        String msrp = JsonPath.read(document, "$.product.variants[0].price");

        Part part = new Part(null, num, title, info, source, null, refImage, partHash, category, null, msrp);
        result.setData(part);

        return result;
    }
}
