package com.github.nramc.dev.journey.api.tts;

/**
 * Public API for the Text-to-Speech module.
 * Other modules (like Journey) can use this interface to synthesize speech from text.
 */
public interface TtsService {

    /**
     * Synthesizes speech from text using default settings.
     *
     * @param text the text to convert to speech
     * @return the synthesized audio as byte array (WAV format)
     */
    byte[] synthesize(String text);

    /**
     * Synthesizes speech from text with specified parameters.
     *
     * @param text        the text to convert to speech
     * @param voice       the voice model to use
     * @param lengthScale length scale parameter (affects speed)
     * @param noiseScale  noise scale parameter
     * @param noiseWScale noise w scale parameter
     * @return the synthesized audio as byte array (WAV format)
     */
    byte[] synthesize(String text, String voice, Double lengthScale, Double noiseScale, Double noiseWScale);

    /**
     * Synthesizes speech from text with specified voice.
     * Uses default values for other parameters.
     *
     * @param text  the text to convert to speech
     * @param voice the voice model to use
     * @return the synthesized audio as byte array (WAV format)
     */
    default byte[] synthesizeWithVoice(String text, String voice) {
        return synthesize(text, voice, null, null, null);
    }

}
