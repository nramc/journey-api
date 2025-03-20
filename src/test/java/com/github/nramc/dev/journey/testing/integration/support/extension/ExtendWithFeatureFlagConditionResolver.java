package com.github.nramc.dev.journey.testing.integration.support.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ExtendWithFeatureFlagConditionResolver extends AbstractConditionalExtensionResolver {

    @Override
    public void handler(ExtensionContext context, Class<? extends Extension> callbackClass) {
        Stream.of(context.getTestClass(), context.getTestMethod())
                .flatMap(Optional::stream)
                .map(element -> element.getAnnotation(ExtendWithFeatureFlagCondition.class))
                .filter(Objects::nonNull)
                .forEach(annotation -> evaluateConditionAndInvokeExtensions(context, annotation, callbackClass));
    }

    private void evaluateConditionAndInvokeExtensions(ExtensionContext context, ExtendWithFeatureFlagCondition extendWith, Class<? extends Extension> targetExtensionType) {
        if (evaluateCondition(context, extendWith)) {
            log.debug("Condition met for feature:[{}] Registering extensions:[{}]", Arrays.toString(extendWith.property()), extendWith.extensions());
            invokeExtensionsIfApplicable(context, extendWith.extensions(), targetExtensionType);
        } else {
            log.debug("Condition not met for feature:[{}]. Skipping extensions.", Arrays.toString(extendWith.property()));
        }
    }

    private boolean evaluateCondition(ExtensionContext context, ExtendWithFeatureFlagCondition extendWith) {
        if (ArrayUtils.isEmpty(extendWith.property())) {
            return true;
        }

        Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
        return Arrays.stream(extendWith.property())
                .anyMatch(property -> environment.getProperty(property, Boolean.class, false));
    }


}
