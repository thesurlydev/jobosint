package com.jobosint.score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ResumeJobMatcher {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "of", "to", "in", "on", "with", "for",
            "at", "by", "from", "up", "about", "as", "into", "like", "through"));

    public static void main(String[] args) throws IOException {
        // Read files
        String resumeText = Files.readString(Paths.get("resume.txt"));
        String jobDescText = Files.readString(Paths.get("job_description.txt"));

        // Extract keywords
        Map<String, Integer> resumeKeywords = extractKeywords(resumeText);
        Map<String, Integer> jobKeywords = extractKeywords(jobDescText);

        // Calculate match score
        double matchScore = calculateMatchScore(resumeKeywords, jobKeywords);

        // Print results
        System.out.println("Match Score: " + matchScore);
        System.out.println("\nTop Resume Keywords:");
        printTopKeywords(resumeKeywords, 10);
        System.out.println("\nTop Job Keywords:");
        printTopKeywords(jobKeywords, 10);
    }

    public static Map<String, Integer> extractKeywords(String text) {
        // Normalize text: lowercase and remove special characters
        text = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ");

        // Split into words
        String[] words = text.split("\\s+");

        // Count word frequencies (skip stop words)
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String word : words) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        return wordFreq;
    }

    public static double calculateMatchScore(
            Map<String, Integer> resumeKeywords,
            Map<String, Integer> jobKeywords) {

        // Find common keywords
        Set<String> commonKeywords = new HashSet<>(resumeKeywords.keySet());
        commonKeywords.retainAll(jobKeywords.keySet());

        // Calculate total weight of matching keywords
        double matchWeight = 0.0;
        for (String keyword : commonKeywords) {
            matchWeight += Math.min(resumeKeywords.get(keyword), jobKeywords.get(keyword));
        }

        // Normalize by job keywords (what they're looking for)
        double totalJobWeight = jobKeywords.values().stream()
                .mapToInt(Integer::intValue).sum();

        return (totalJobWeight > 0) ? (matchWeight / totalJobWeight) * 100.0 : 0.0;
    }

    public static void printTopKeywords(Map<String, Integer> keywords, int limit) {
        keywords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}