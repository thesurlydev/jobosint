package com.jobosint.model.form;

import lombok.Getter;

@Getter
public class SearchForm {
    private String query;

    public void setQuery(String query) {
        this.query = query;
    }
}
