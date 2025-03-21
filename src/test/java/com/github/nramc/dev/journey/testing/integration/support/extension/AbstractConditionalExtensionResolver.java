package com.github.nramc.dev.journey.testing.integration.support.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

@Slf4j
public abstract class AbstractConditionalExtensionResolver implements BeforeAllCallback, BeforeEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterEachCallback, AfterAllCallback {

    public abstract void handler(ExtensionContext context, Class<? extends Extension> callbackClass);

    @Override
    public void beforeAll(ExtensionContext context) {
        handler(context, BeforeAllCallback.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        handler(context, BeforeEachCallback.class);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        handler(context, BeforeTestExecutionCallback.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        handler(context, AfterTestExecutionCallback.class);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        handler(context, AfterEachCallback.class);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        handler(context, AfterAllCallback.class);
    }

    protected void invokeExtensionsIfApplicable(ExtensionContext context, Class<? extends Extension>[] extensions, Class<? extends Extension> targetExtensionType) {
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
