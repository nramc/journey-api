package com.github.nramc.dev.journey.api.tts.service;

import com.github.nramc.dev.journey.api.tts.TtsService;
import com.github.nramc.dev.journey.api.tts.client.PiperHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link TtsService} using the Piper TTS server.
 * This service provides text-to-speech capabilities by delegating to the Piper HTTP client.
 */
@Slf4j
@RequiredArgsConstructor
public class PiperTtsService implements TtsService {

    private final PiperHttpClient piperHttpClient;

    /**
     * Synthesizes speech from text using default settings.
     *
     * @param text the text to convert to speech
     * @return the synthesized audio as byte array
     */
    @Override
    public byte[] synthesize(String text) {
        return synthesize(text, null, null, null, null);
    }

    /**
     * Synthesizes speech from text with specified parameters.
     *
     * @param text        the text to convert to speech
     * @param voice       the voice model to use (null for default)
     * @param lengthScale length scale parameter (null for default)
     * @param noiseScale  noise scale parameter (null for default)
     * @param noiseWScale noise w scale parameter (null for default)
     * @return the synthesized audio as byte array
     */
    @Override
    public byte[] synthesize(String text, String voice, Double lengthScale,
                             Double noiseScale, Double noiseWScale) {
        log.info("Synthesizing speech for text of length: {}", text.length());

        var synthesize = piperHttpClient.synthesize(text, voice, lengthScale, noiseScale, noiseWScale);
        log.info("Speech synthesis completed, audio size: {} bytes", synthesize.length);
        return synthesize;
    }

}
