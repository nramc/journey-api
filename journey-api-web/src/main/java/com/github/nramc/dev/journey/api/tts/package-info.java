/**
 * Text-to-Speech module — provides TTS capabilities using Piper server.
 *
 * <p>Public API:
 * <ul>
 *   <li>{@link com.github.nramc.dev.journey.api.tts.TtsService} — interface for text-to-speech conversion</li>
 * </ul>
 *
 * <p>REST API:
 * <ul>
 *   <li>POST /api/tts — synthesize speech from text</li>
 * </ul>
 *
 * <p>This module communicates with the Piper TTS server via HTTP.
 *
 * <p>Depends only on {@code shared} (for exceptions and utilities).
 * Other modules (like {@code journey}) can depend on this module to use TTS capabilities.
 */
@ApplicationModule(
        displayName = "Text-to-Speech",
        allowedDependencies = {"shared"}
)
package com.github.nramc.dev.journey.api.tts;

import org.springframework.modulith.ApplicationModule;
