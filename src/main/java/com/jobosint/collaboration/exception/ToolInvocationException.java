package com.jobosint.collaboration.exception;

public class ToolInvocationException extends RuntimeException {
    public ToolInvocationException(String s, Exception e) {
        super(s, e);
    }
}
