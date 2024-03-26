package com.jobosint.model.lever;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown=true)
@Data
public class LeverLinkedData {
    private HiringOrganization hiringOrganization;
    private String title;
    private String description;
}
