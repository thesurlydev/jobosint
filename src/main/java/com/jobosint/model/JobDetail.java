package com.jobosint.model;

import java.util.List;

public record JobDetail(JobAndCompany jobDetail, List<Note> notes) {
}
