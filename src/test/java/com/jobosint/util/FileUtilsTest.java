package com.jobosint.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class FileUtilsTest {

    @Test
    public void createCruiserYardLinksFile() {
        com.jobosint.util.FileUtils.createLinksFile(
                "https://www.cruiseryard.com/fj60fj62-series/?page=",
                19,
                "/home/shane/projects/jobosint/content/cruiseryard-links.txt"
        );
    }

    @Test
    public void createIh8mudLinksFile() {
        com.jobosint.util.FileUtils.createLinksFile(
                "https://forum.ih8mud.com/forums/60-series-wagons.27/page-",
                2104,
                "/home/shane/projects/jobosint/content/ih8mud-links.txt"
        );
    }

    @Test
    public void renameAllFilesTest() throws Exception {
        FileUtils.renameAllFiles("/home/shane/projects/jobosint/content/cruiseryard", null, "l", false);
    }
}
