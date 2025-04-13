package com.jobosint.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {

    public static List<String> readAsStrings(String path) {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            log.error("Error reading file {}", path, e);
            return List.of();
        }
    }

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

    private static void checkDirExists(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    public static void saveToFile(String content, Path filePath) throws IOException {

        // verify directory exists
        Path parentDir = filePath.getParent();
        checkDirExists(parentDir);

        // write content to filePath
        Files.writeString(filePath, content);
    }
    public static void appendToFile(Path filePath, String text) {
        File file = filePath.toFile();
        try (FileWriter fr = new FileWriter(file, true); BufferedWriter br = new BufferedWriter(fr); PrintWriter pr = new PrintWriter(br)) {
            pr.println(text);
        } catch (IOException e) {
            log.error("Error appending to file {}", filePath, e);
        }
    }

    public static void renameAllFiles(Path dir, String prepend, String append, boolean dryRun) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
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
                File newFile = dir.resolve(newFilename).toFile();
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

    public static void createIndexedLinksFile(String baseUrl, int max, Path linksFilePath) {
        IntStream.rangeClosed(1, max)
                .mapToObj(i -> baseUrl + i)
                .forEach(url -> appendToFile(linksFilePath, url));
    }

    /**
     * Reads the contents of a file as a string.
     *
     * @param filePath the path to the file to read
     * @return the contents of the file as a string
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String readFromFile(Path filePath) throws IOException {
        return Files.readString(filePath);
    }
}
