package com.jobosint.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public record Company(@Id UUID id,
                      String name,
                      @Column("website_url") @JsonProperty("website_url") String websiteUrl

                      ) {


}
