package com.github.nramc.dev.journey.testing.integration.support.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ExtendWithEnvConditionResolver extends AbstractConditionalExtensionResolver {

    @Override
    public void handler(ExtensionContext context, Class<? extends Extension> callbackClass) {
        Stream.of(context.getTestClass(), context.getTestMethod())
                .flatMap(Optional::stream)
                .map(element -> element.getAnnotation(ExtendWithEnvCondition.class))
                .filter(Objects::nonNull)
                .forEach(annotation -> evaluateConditionAndInvokeExtensions(context, annotation, callbackClass));
    }

    private void evaluateConditionAndInvokeExtensions(ExtensionContext context, ExtendWithEnvCondition extendWith, Class<? extends Extension> targetExtensionType) {
        if (evaluateCondition(extendWith)) {
            log.debug("Condition met for env variables:[{}] Registering extensions:[{}]", Arrays.toString(extendWith.variables()), extendWith.extensions());
            invokeExtensionsIfApplicable(context, extendWith.extensions(), targetExtensionType);
        } else {
            log.debug("Condition not met for env variables:[{}]. Skipping extensions.", Arrays.toString(extendWith.variables()));
        }
    }

    private boolean evaluateCondition(ExtendWithEnvCondition extendWith) {
        if (ArrayUtils.isEmpty(extendWith.variables())) {
            return true;
        }

        return Arrays.stream(extendWith.variables())
                .map(variable -> variable.split("="))
                .anyMatch(keyValueEntry -> StringUtils.equals(System.getenv(keyValueEntry[0]), keyValueEntry[1]));
    }


}
