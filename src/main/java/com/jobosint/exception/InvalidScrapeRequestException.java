package com.jobosint.exception;

public class InvalidScrapeRequestException extends RuntimeException {
    public InvalidScrapeRequestException(String message) {
        super(message);
    }

    public InvalidScrapeRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
