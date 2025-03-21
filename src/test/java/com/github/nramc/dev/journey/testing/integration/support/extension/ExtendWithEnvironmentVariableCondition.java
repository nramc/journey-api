package com.github.nramc.dev.journey.testing.integration.support.extension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(ExtendWithEnvironmentVariableConditionResolver.class)
public @interface ExtendWithEnvironmentVariableCondition {

    String[] variables() default {};

    Class<? extends Extension>[] extensions() default {};
}
