package com.jobosint.model.form;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AttributeForm {
    private String name;
    private Set<String> values;
}
