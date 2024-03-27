package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.util.Set;
import java.util.UUID;

public record Attribute(@Id UUID id,
                        String name,
                        Set<String> values) {

}
