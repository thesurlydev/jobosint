package com.jobosint.model;

import java.math.BigDecimal;

public record VendorPart(Vendor vendor, Part part, BigDecimal price, String sku, boolean available, PartCondition condition, String vendorPartUrl) {

    public Price priceObj() {
        return new Price(null, this.vendor().id(), part.partNumber(), this.price, this.available(), this.sku(), this.vendorPartUrl(), this.condition());
    }
}
