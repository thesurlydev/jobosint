package com.jobosint.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class FileUtilsTest {

    @Test
    public void readAsStrings() {
        var strings = FileUtils.readAsStrings("src/main/resources/data/tlds.txt");
        assertFalse(strings.isEmpty());
    }

    @Test
    @Disabled
    public void createCruiserYardLinksFile() {
        com.jobosint.util.FileUtils.createLinksFile(
                "https://www.cruiseryard.com/fj60fj62-series/?page=",
                19,
                "/home/shane/projects/jobosint/content/cruiseryard-links.txt"
        );
    }

    @Test
    @Disabled
    public void createIh8mudLinksFile() {
        com.jobosint.util.FileUtils.createLinksFile(
                "https://forum.ih8mud.com/forums/60-series-wagons.27/page-",
                2104,
                "/home/shane/projects/jobosint/content/ih8mud-links.txt"
        );
    }

    @Test
    @Disabled
    public void renameAllFilesTest() throws Exception {
        FileUtils.renameAllFiles("/home/shane/projects/jobosint/content/cruiseryard", null, "l", false);
    }
}
