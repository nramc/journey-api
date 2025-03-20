package com.github.nramc.dev.journey.testing.integration.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class MyBeforeAllExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        log.info("MyBeforeAllExtension > Before all executed");
    }
}
