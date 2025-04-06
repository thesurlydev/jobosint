package com.jobosint.problem;

public record YouTubeChannel(String title,
                             String description,
                             String url,
                             Long numberOfSubscribers,
                             Integer numberOfVideos) {
}
