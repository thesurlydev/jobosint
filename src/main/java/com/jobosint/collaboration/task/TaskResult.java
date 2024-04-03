package com.jobosint.collaboration.task;

public class TaskResult {
    private final Object data;
    private final String dataType;

    public TaskResult(Object data) {
        this.data = data;
        this.dataType = data.getClass().getSimpleName();
    }

    public Object getData() {
        return data;
    }

    public String getDataType() {
        return dataType;
    }
}
