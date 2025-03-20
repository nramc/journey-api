package com.github.nramc.dev.journey.testing.integration.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class MyBeforeTestMethodExtension implements BeforeTestExecutionCallback {


    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        log.info("MyBeforeTestMethodExtension > beforeTestExecution executed");
    }
}
