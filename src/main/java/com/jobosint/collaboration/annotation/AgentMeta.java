package com.jobosint.collaboration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // Apply to classes
public @interface AgentMeta {
    String goal();
    String[] tools() default {};
}
