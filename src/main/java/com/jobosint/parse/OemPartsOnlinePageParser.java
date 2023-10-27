package com.jobosint.parse;

import com.jobosint.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;

import static com.jobosint.model.PartClassification.OEM;

@Component
@Slf4j
public class OemPartsOnlinePageParser implements Parser<Path, List<VendorPart>> {
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

        // Body
        Elements categoryEls = doc.getElementsByClass("category-group")
                .select("a.category")
                .select("[aria-expanded=true]");

        String category;
        if (!categoryEls.isEmpty()) {
            category = categoryEls.getFirst().attr("title");
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
            subcategory = subcategoryEls.getFirst().text();
        } else {
            subcategory = null;
            log.warn("No subcategory found for {}", path);
        }

        Elements partContainers = doc.getElementsByClass("oem-assemblies-module")
                .select("div.part-group div.part-group-container div.part-row div.product-details-col");

        if (!partContainers.isEmpty()) {
            List<VendorPart> vendorParts = partContainers.stream()
                    .map(pc -> parsePartContainer(pc, category, subcategory))
                    .toList();
            result.setData(vendorParts);
        }

        return result;
    }

    private VendorPart parsePartContainer(Element partContainer, String category, String subcategory) {

        String name = partContainer.select("strong.product-title").text();
        String partNumber = partContainer.select("div.product-partnum").text();
        String description = partContainer.select("div.product-more-info").text();
        Elements discontinuedEl = partContainer.select("strong.cannot-purchase");
        boolean available = !discontinuedEl.isEmpty();

        String msrpPriceStr = partContainer.select("div.list-price").text();
        BigDecimal msrpPrice = null;
        if (!msrpPriceStr.isEmpty()) {
            msrpPriceStr = msrpPriceStr.substring(6);
            msrpPriceStr = msrpPriceStr.replace(",", "");
            try {
                msrpPrice = new BigDecimal(msrpPriceStr).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("Error parsing msrp price: {}", msrpPriceStr, e);
            }
        }

        Part part = new Part(null, partNumber, name, description, Manufacturer.TOYOTA.id(), category, subcategory, msrpPrice, OEM);

        String priceStr = partContainer.select("div.sale-price").text();
        BigDecimal price = null;
        if (!priceStr.isEmpty()) {
            try {
                price = new BigDecimal(priceStr.substring(1)).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("Error parsing price: {}", priceStr, e);
            }
        }
        String sku = partNumber;
        String vendorPartUrlPath = partContainer.select("strong.product-title a").attr("href");
        String vendorPartUrl = Vendor.OEM_PARTS_ONLINE.baseUrl() + vendorPartUrlPath;

        return new VendorPart(Vendor.OEM_PARTS_ONLINE, part, price, sku, available, PartCondition.NEW, vendorPartUrl);
    }
}
