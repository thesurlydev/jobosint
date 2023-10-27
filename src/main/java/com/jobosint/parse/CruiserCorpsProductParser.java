package com.jobosint.parse;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jobosint.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.jobosint.model.PartCondition.NEW;

@Component
@Slf4j
public class CruiserCorpsProductParser implements Parser<Path, List<VendorPart>> {
    @Override
    public ParseResult<List<VendorPart>> parse(Path path) {

        log.info("Parsing: {}", path);
        ParseResult<List<VendorPart>> result = new ParseResult<>();

        String json;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

        String title = JsonPath.read(document, "$.product.title");
        String category = JsonPath.read(document, "$.product.product_type");
        String info = JsonPath.read(document, "$.product.body_html");
        String sku = JsonPath.read(document, "$.product.variants[0].sku");
        String priceStr = JsonPath.read(document, "$.product.variants[0].price");
        BigDecimal price = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);

        // TODO update these
        String vendorPartUrl = null;
        boolean available = true;
        String subcategory = null;

        // TODO NO CLEAN WAY TO ASSOCIATE CRUISERCORPS PARTS WITH TOYOTA PART NUMBERS!!!

        Part part = new Part(null, sku, title, info, Manufacturer.TOYOTA.id(), category, subcategory, null, PartClassification.OEM);

        VendorPart vendorPart = new VendorPart(Vendor.CRUISER_CORPS, part, price, sku, available, NEW, vendorPartUrl);
        result.setData(List.of(vendorPart));
        return result;
    }
}
