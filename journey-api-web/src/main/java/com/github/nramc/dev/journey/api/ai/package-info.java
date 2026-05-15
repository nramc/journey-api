/**
 * AI module — AI-powered REST resources (chat, narration enhancement).
 *
 * <p>Depends only on {@code shared}.  Paths are hardcoded strings (per project convention)
 * and are covered by the existing wildcard security rule in {@code WebSecurityConfig}.
 */
@ApplicationModule(
        displayName = "AI",
        allowedDependencies = {"shared"}
)
package com.github.nramc.dev.journey.api.ai;

import org.springframework.modulith.ApplicationModule;
