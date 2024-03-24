package com.jobosint.convert;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.springframework.stereotype.Component;

@Component
public class HtmlToMarkdownConverter {
    public String convertToMarkdown(String html) {
        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
        return converter.convert(html);
    }
}
