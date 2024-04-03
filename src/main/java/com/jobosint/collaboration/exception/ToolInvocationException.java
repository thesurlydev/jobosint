package com.jobosint.collaboration.exception;

import com.jobosint.collaboration.tool.ToolMetadata;

public class ToolInvocationException extends RuntimeException {
    public ToolInvocationException(String msg) {
        super(msg) ;
    }

    public ToolInvocationException(String msg, Exception e) {
        super(msg, e);
    }
}
