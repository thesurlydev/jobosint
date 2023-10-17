package com.jobosint.listener;

import com.jobosint.event.PersistVendorPartEvent;
import com.jobosint.model.Part;
import com.jobosint.model.PartCondition;
import com.jobosint.model.Price;
import com.jobosint.model.VendorPart;
import com.jobosint.service.PartService;
import com.jobosint.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistVendorPartEventListener implements ApplicationListener<PersistVendorPartEvent> {

    private final PartService partService;
    private final PriceService priceService;

    @Override
    public void onApplicationEvent(PersistVendorPartEvent event) {
        VendorPart vendorPart = event.getPart();
        log.info(vendorPart.toString());

        Part part = vendorPart.part();
        Part savedPart = partService.savePart(part);
        if (savedPart == null) {
            log.warn("Part not saved: {}", part);
            return;
        } else {
            log.info("Saved part: {}", savedPart);
            // only save price if available
            if (vendorPart.available()) {
                BigDecimal priceVal = vendorPart.price();
                Price price = new Price(null, vendorPart.vendor().id(), savedPart.id(), savedPart.name(), priceVal, vendorPart.available(), vendorPart.sku(), null, PartCondition.NEW);
                Price savedPrice = priceService.savePrice(price);
                log.info("Saved price: {}", savedPrice);
            }
        }
    }
}
