package com.jobosint.collaboration.task;

import java.util.List;

public record DeconstructedTask(String originalTask, List<String> subtasks) {
}
