package com.jobosint.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {

    public static void writeToFile(String path, String content) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
        } catch (IOException e) {
            log.error("Error writing to file {}", path, e);
        }
    }

    public static void appendToFile(String filePath, String text) {
        File file = new File(filePath);
        try (FileWriter fr = new FileWriter(file, true); BufferedWriter br = new BufferedWriter(fr); PrintWriter pr = new PrintWriter(br)) {
            pr.println(text);
        } catch (IOException e) {
            log.error("Error appending to file {}", filePath, e);
        }
    }

    public static void renameAllFiles(String dir, String prepend, String append, boolean dryRun) throws IOException {
        try (Stream<Path> paths = Files.walk(Path.of(dir))) {
            paths.forEach(path -> {
                File oldFile = path.toFile();
                if (oldFile.isDirectory()) {
                    return;
                }
                String newFilename = oldFile.getName();
                if (prepend != null) {
                    newFilename = prepend + newFilename;
                }
                if (append != null) {
                    newFilename = newFilename + append;
                }
                File newFile = new File(dir, newFilename);
                if (dryRun) {
                    log.info("[DRY RUN] Renaming {} to {}", oldFile, newFile);
                } else {
                    boolean success = oldFile.renameTo(newFile);
                    if (success) {
                        log.info("Renamed {} to {}", oldFile, newFile);
                    } else {
                        log.error("Error renaming {} to {}", oldFile, newFile);
                    }
                }
            });
        }
    }

    public static void createLinksFile(String baseUrl, int max, String linksFilePath) {
        IntStream.rangeClosed(1, max)
                .mapToObj(i -> baseUrl + i)
                .forEach(url -> appendToFile(linksFilePath, url));
    }
}
