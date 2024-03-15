package com.jobosint.model;


import lombok.Data;

import java.util.List;

@Data
public class JobDescription {
    private String title;
    private String rawHead;
    private String rawBody;
    private String rawText;
    private String markdownBody;
    private List<String> requirements;
    private String aboutRole;
    private String aboutTeam;
}
