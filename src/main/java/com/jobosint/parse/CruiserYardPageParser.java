package com.jobosint.parse;

import com.jobosint.model.Part;
import com.jobosint.util.ParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class CruiserYardPageParser implements Parser<Path, List<Part>> {

    public ParseResult<List<String>> getDetailPageUrls(Path path) {
        ParseResult<List<String>> result = new ParseResult<>();
        Document doc;
        try {
            doc = ParseUtils.getDocumentFromPath(path);
        } catch (IOException e) {
            result.addError("Error parsing: " + path + "; " + e.getMessage());
            return result;
        }

        // Body
        Elements categoryEls = doc.getElementsByClass("card-name");
        List<String> detailPages = categoryEls.stream()
                .map(el -> el.select("a").attr("href"))
                .toList();

        result.setData(detailPages);
        return result;
    }

    @Override
    public ParseResult<List<Part>> parse(Path path) {






        return null;
    }

//    for a given parts site:
//    -> part list pages (file with list of urls)
//       -> download each and get detail urls
//          -> download detail pages (file with list of urls)
//             -> parse each part detail page
//                -> save part detail to db

// define map functions for each part property


}
