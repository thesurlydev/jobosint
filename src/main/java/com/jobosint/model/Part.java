package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record Part(
        @Id UUID id,
        String num,
        String title,
        String info,
        String source,
        String refCode,
        String refImage) {

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%s", this.num, this.title, this.info, this.source, this.refCode, this.refImage);
    }
}
