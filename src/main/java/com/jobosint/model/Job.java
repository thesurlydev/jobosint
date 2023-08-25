package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record Job(@Id UUID id, String title, String url) {

}
