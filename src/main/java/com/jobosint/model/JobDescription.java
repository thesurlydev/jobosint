package com.jobosint.model;


import lombok.Data;

import java.util.List;

@Data
public class JobDescription {
    private String title;
    private List<String> requirements;
    private String aboutRole;
    private String aboutTeam;
}
