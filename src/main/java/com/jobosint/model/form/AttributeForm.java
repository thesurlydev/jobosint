package com.jobosint.model.form;

import com.jobosint.model.AttributeDetail;
import lombok.Data;

import java.util.UUID;

@Data
public class AttributeForm {
    private UUID id;
    private String name;
    private String valuesCsv;

    public AttributeForm() {
    }

    public AttributeForm(AttributeDetail attributeDetail) {
        this.id = attributeDetail.attribute().id();
        this.name = attributeDetail.attribute().name();
    }
}
