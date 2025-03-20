package com.github.nramc.dev.journey.testing.integration.support.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ExtendWithProfileConditionResolver extends AbstractConditionalExtensionResolver implements BeforeAllCallback, BeforeEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterEachCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        handleEvent(context, BeforeAllCallback.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        handleEvent(context, BeforeEachCallback.class);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        handleEvent(context, BeforeTestExecutionCallback.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        handleEvent(context, AfterTestExecutionCallback.class);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        handleEvent(context, AfterEachCallback.class);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        handleEvent(context, AfterAllCallback.class);
    }

    private static void handleEvent(ExtensionContext context, Class<? extends Extension> callbackClass) {
        Stream.of(context.getTestClass(), context.getTestMethod())
                .flatMap(Optional::stream)
                .map(element -> element.getAnnotation(ExtendWithProfileCondition.class))
                .filter(Objects::nonNull)
                .forEach(annotation -> evaluateConditionAndInvokeExtensions(context, annotation, callbackClass));
    }

    private static void evaluateConditionAndInvokeExtensions(ExtensionContext context, ExtendWithProfileCondition extendWith, Class<? extends Extension> targetExtensionType) {
        if (evaluateCondition(context, extendWith)) {
            log.debug("Condition met for profile:[{}] Registering extensions:[{}]", Arrays.toString(extendWith.profiles()), extendWith.extensions());
            invokeExtensionsIfApplicable(context, extendWith.extensions(), targetExtensionType);
        } else {
            log.debug("Condition not met for profile:[{}]. Skipping extensions.", Arrays.toString(extendWith.profiles()));
        }
    }

    private static boolean evaluateCondition(ExtensionContext context, ExtendWithProfileCondition extendWith) {
        Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
        return ArrayUtils.isEmpty(extendWith.profiles()) ||
                Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> ArrayUtils.contains(extendWith.profiles(), profile));
    }

    private static void invokeExtensionsIfApplicable(ExtensionContext context, Class<? extends Extension>[] extensions, Class<? extends Extension> targetExtensionType) {
        Arrays.stream(extensions).filter(targetExtensionType::isAssignableFrom).forEach(extensionClass -> {
            try {
                Extension extensionInstance = extensionClass.getDeclaredConstructor().newInstance();
                switch (extensionInstance) {
                    case BeforeAllCallback callback -> callback.beforeAll(context);
                    case BeforeEachCallback callback -> callback.beforeEach(context);
                    case BeforeTestExecutionCallback callback -> callback.beforeTestExecution(context);
                    case AfterTestExecutionCallback callback -> callback.afterTestExecution(context);
                    case AfterEachCallback callback -> callback.afterEach(context);
                    case AfterAllCallback callback -> callback.afterAll(context);
                    default -> log.warn("Unsupported extension: {}", extensionClass);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to instantiate and invoke extension: " + extensionClass, ex);
            }
        });
    }
}
