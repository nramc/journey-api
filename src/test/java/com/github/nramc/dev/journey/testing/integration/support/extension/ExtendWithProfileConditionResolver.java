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
public class ExtendWithProfileConditionResolver extends AbstractConditionalExtensionResolver {

    @Override
    public void handler(ExtensionContext context, Class<? extends Extension> callbackClass) {
        Stream.of(context.getTestClass(), context.getTestMethod())
                .flatMap(Optional::stream)
                .map(element -> element.getAnnotation(ExtendWithProfileCondition.class))
                .filter(Objects::nonNull)
                .forEach(annotation -> evaluateConditionAndInvokeExtensions(context, annotation, callbackClass));
    }

    private void evaluateConditionAndInvokeExtensions(ExtensionContext context, ExtendWithProfileCondition extendWith, Class<? extends Extension> targetExtensionType) {
        if (evaluateCondition(context, extendWith)) {
            log.debug("Condition met for profile:[{}] Registering extensions:[{}]", Arrays.toString(extendWith.profiles()), extendWith.extensions());
            invokeExtensionsIfApplicable(context, extendWith.extensions(), targetExtensionType);
        } else {
            log.debug("Condition not met for profile:[{}]. Skipping extensions.", Arrays.toString(extendWith.profiles()));
        }
    }

    private boolean evaluateCondition(ExtensionContext context, ExtendWithProfileCondition extendWith) {
        Environment environment = SpringExtension.getApplicationContext(context).getEnvironment();
        return ArrayUtils.isEmpty(extendWith.profiles()) ||
                Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> ArrayUtils.contains(extendWith.profiles(), profile));
    }


}
