package com.jobosint.collaboration.exception;

public class ToolNotFoundException extends RuntimeException {
    public ToolNotFoundException(String msg) {
        super(msg);
    }

    public ToolNotFoundException(String msg, Exception e) {
        super(msg, e);
    }
}