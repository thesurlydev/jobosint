package com.jobosint.collaboration.exception;

public class ToolInvocationException extends RuntimeException {
    public ToolInvocationException(String msg) {
        super(msg) ;
    }

    public ToolInvocationException(String msg, Exception e) {
        super(msg, e);
    }
}
