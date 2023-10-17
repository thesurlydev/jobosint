package com.jobosint.model;

import java.math.BigDecimal;

public record VendorPart(Vendor vendor, Part part, BigDecimal price, String sku, boolean available) {
}
