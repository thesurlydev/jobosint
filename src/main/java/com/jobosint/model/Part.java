package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public record Part(
        @Id UUID id,
        String num, // vendor specific
        String title,
        String info,
        String source,
        String refCode,
        String refImage,
        String partHash,
        String category,
        String subcategory,
        String msrp,
        String price,
        String supplier,
        String vendor,
        String sku // supplier specific
) {

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%s", this.num, this.title, this.info, this.source, this.refCode, this.refImage);
    }

    public String description() {
        return this.title + " " + this.info;
    }

    public static String calcHash(String... parts) {
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            out.append(part);
        }
        return Base64.getEncoder().withoutPadding().encodeToString(out.toString().getBytes());
    }

    public Path localRefImagePath(Path targetImageDir) {
        // specific to oeampartsonline
        String targetFilename = refImage.substring("//dz310nzuyimx0.cloudfront.net/strapr1/".length() + 1);
        return Paths.get(targetImageDir.toString(), targetFilename);
    }
}
