package com.jobosint.parse;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jobosint.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.jobosint.model.PartClassification.OEM;
import static com.jobosint.model.Vendor.TOYOTA_PARTS_DEAL;

@Component
@Slf4j
public class ToyotaPartsDealPageParser implements Parser<Path, List<VendorPart>> {


    @Override
    public ParseResult<List<VendorPart>> parse(Path path) {
        log.info("Parsing: {}", path);
        ParseResult<List<VendorPart>> result = new ParseResult<>();
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

        List<VendorPart> parsedVendorParts = new ArrayList<>();
        for (List<Map<String, Object>> arr : list) {
            Map<String, Object> map = arr.getFirst();
            String partNumber = (String) map.get("partNumber");
            String description = (String) map.get("mainDesc");

            Map<String, String> priceInfo = (Map<String, String>) map.get("priceInfo");
            String msrpStr = priceInfo.get("retail");
            boolean available = false;
            BigDecimal msrp = null;
            try {
                msrp = new BigDecimal(msrpStr).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                if (msrpStr.equals("--")) {
                    available = true;
                } else {
                    log.warn("Error parsing msrp '{}' for part number: {}", msrpStr, partNumber);
                }
            }
            String priceStr= priceInfo.get("price");
            BigDecimal price = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);
            UUID manufacturerId = Manufacturer.TOYOTA.id();

            // note: for ToyotaPartsDeal, sku is the same as partNumber

            Part part = new Part(null, partNumber, description, null, manufacturerId,null, null, msrp, OEM);

            String vendorPartUrlPath = (String) map.get("url");
            String vendorPartUrl = Vendor.TOYOTA_PARTS_DEAL.baseUrl() + vendorPartUrlPath;

            VendorPart vendorPart = new VendorPart(TOYOTA_PARTS_DEAL, part, price, partNumber, available, PartCondition.NEW, vendorPartUrl);

            parsedVendorParts.add(vendorPart);
        }

        result.setData(parsedVendorParts);

        return result;
    }
}
