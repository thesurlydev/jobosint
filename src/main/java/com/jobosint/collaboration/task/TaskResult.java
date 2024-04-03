package com.jobosint.collaboration.task;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class TaskResult {
    private final Object data;
    private final String dataType;

    public TaskResult(Object data) {
        this.data = data;
        this.dataType = data.getClass().getSimpleName();
    }

}
