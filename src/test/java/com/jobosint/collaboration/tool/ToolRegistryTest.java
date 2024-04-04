package com.jobosint.collaboration.tool;

import com.jobosint.collaboration.task.Task;
import com.jobosint.collaboration.agent.example.Greeter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ToolRegistryTest {

    @Autowired ToolRegistry toolRegistry;

    @Test
    public void testGetToolArgsNoArg() throws NoSuchMethodException {
        var task = new Task("Say hello");
        Method method = Greeter.class.getMethod("sayHello");
        ToolMetadata toolMetadata = new ToolMetadata("SayHello", "Be friendly and say hello", method);
        Object args = toolRegistry.getToolArgs(toolMetadata, task);
        assertNull(args);
    }
}
