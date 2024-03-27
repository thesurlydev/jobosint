package com.jobosint.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;

public class ConversionUtils {
    public static String convertToMarkdown(String html) {
        return FlexmarkHtmlConverter.builder().build().convert(html);
    }

    public static String convertToHtml(String markdownContent) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(markdownContent));
    }
}
