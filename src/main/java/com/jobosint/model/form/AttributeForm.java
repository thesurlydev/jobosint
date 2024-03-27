package com.jobosint.model.form;

import lombok.Data;

import java.util.UUID;

@Data
public class AttributeForm {
    private UUID id;
    private String name;
    private String valuesCsv;
}
