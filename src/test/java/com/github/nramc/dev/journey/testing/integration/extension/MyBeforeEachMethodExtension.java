package com.github.nramc.dev.journey.testing.integration.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class MyBeforeEachMethodExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        log.info("MyBeforeEachMethodExtension > Before each executed");
    }
}
