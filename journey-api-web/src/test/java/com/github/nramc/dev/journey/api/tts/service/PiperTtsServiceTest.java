package com.github.nramc.dev.journey.api.tts.service;

import com.github.nramc.dev.journey.api.tts.client.PiperHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PiperTtsServiceTest {

    @Mock
    private PiperHttpClient piperHttpClient;

    @InjectMocks
    private PiperTtsService ttsService;

    private final byte[] mockAudioData = "mock-audio-bytes".getBytes();

    // ==================== synthesize(String text) Tests ====================

    @Test
    void synthesize_withTextOnly_shouldDelegateToClientWithNullParameters() {
        // Given
        String text = "Hello World";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text);

        // Then
        assertThat(result)
                .as("Result from synthesize with text only")
                .isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, null, null, null, null);
    }

    @Test
    void synthesize_withEmptyText_shouldStillCallClient() {
        // Given
        String emptyText = "";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(emptyText);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(emptyText, null, null, null, null);
    }

    @Test
    void synthesize_withLongText_shouldHandleLargeInput() {
        // Given
        String longText = "a".repeat(10000);
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(longText);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(
                eq(longText), any(), any(), any(), any()
        );
    }

    @Test
    void synthesize_withSpecialCharacters_shouldHandleUnicode() {
        // Given
        String unicodeText = "Hello 世界! Привет мир! 🌍";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(unicodeText);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(
                eq(unicodeText),
                any(), any(), any(), any()
        );
    }

    // ==================== synthesize(String, String, Double, Double, Double) Tests ====================

    @Test
    void synthesize_withAllParameters_shouldPassAllToClient() {
        // Given
        String text = "Test text";
        String voice = "en_US-amy-medium";
        Double lengthScale = 1.5;
        Double noiseScale = 0.5;
        Double noiseWScale = 0.6;
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, voice, lengthScale, noiseScale, noiseWScale);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, voice, lengthScale, noiseScale, noiseWScale);
    }

    @Test
    void synthesize_withPartialParameters_shouldPassProvidedValues() {
        // Given
        String text = "Test";
        String voice = "en_US-lessac-medium";
        // lengthScale, noiseScale, noiseWScale are null
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, voice, null, null, null);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, voice, null, null, null);
    }

    @Test
    void synthesize_withMixedParameters_shouldPassCorrectValues() {
        // Given
        String text = "Test";
        Double lengthScale = 2.0;
        Double noiseWScale = 0.9;
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, null, lengthScale, null, noiseWScale);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, null, lengthScale, null, noiseWScale);
    }

    // ==================== synthesizeWithVoice Tests ====================

    @Test
    void synthesizeWithVoice_shouldCallSynthesizeWithVoiceAndNullParameters() {
        // Given
        String text = "Test text";
        String voice = "en_US-amy-medium";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesizeWithVoice(text, voice);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, voice, null, null, null);
    }

    @Test
    void synthesizeWithVoice_withNullVoice_shouldPassNull() {
        // Given
        String text = "Test";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesizeWithVoice(text, null);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(text, null, null, null, null);
    }

    // ==================== Multiple Calls Tests ====================

    @Test
    void synthesize_shouldBeCallableMultipleTimes() {
        // Given
        String text1 = "First text";
        String text2 = "Second text";
        String text3 = "Third text";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result1 = ttsService.synthesize(text1);
        byte[] result2 = ttsService.synthesize(text2);
        byte[] result3 = ttsService.synthesize(text3);

        // Then
        assertThat(result1).isEqualTo(mockAudioData);
        assertThat(result2).isEqualTo(mockAudioData);
        assertThat(result3).isEqualTo(mockAudioData);

        verify(piperHttpClient, times(3)).synthesize(any(), any(), any(), any(), any());
    }

    @Test
    void synthesize_withDifferentVoices_shouldCallClientForEach() {
        // Given
        String text = "Test";
        String[] voices = {"en_US-lessac-medium", "en_US-amy-medium", "en_US-libritts-high"};
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        for (String voice : voices) {
            ttsService.synthesizeWithVoice(text, voice);
        }

        // Then
        verify(piperHttpClient, times(3)).synthesize(
                eq(text),
                any(),
                eq(null),
                eq(null),
                eq(null)
        );
    }

    // ==================== Error Handling Tests ====================

    @Test
    void synthesize_whenClientThrowsException_shouldPropagateException() {
        // Given
        String text = "Error test";
        RuntimeException expectedException = new RuntimeException("Piper server error");
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any()))
                .thenThrow(expectedException);

        // When & Then
        assertThatThrownBy(() -> ttsService.synthesize(text))
                .as("Exception from synthesize")
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Piper server error");

        verify(piperHttpClient).synthesize(eq(text), any(), any(), any(), any());
    }

    @Test
    void synthesize_withAllParameters_whenClientThrowsException_shouldPropagate() {
        // Given
        String text = "Test";
        String voice = "en_US-amy-medium";
        Double lengthScale = 1.5;
        Double noiseScale = 0.5;
        Double noiseWScale = 0.6;

        IllegalStateException expectedException = new IllegalStateException("Connection failed");
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any()))
                .thenThrow(expectedException);

        // When & Then
        assertThatThrownBy(() ->
                ttsService.synthesize(text, voice, lengthScale, noiseScale, noiseWScale)
        )
                .as("Exception from synthesize with all parameters")
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Connection failed");

        verify(piperHttpClient).synthesize(
                text, voice, lengthScale, noiseScale, noiseWScale
        );
    }

    // ==================== Edge Cases Tests ====================

    @Test
    void synthesize_withZeroLengthScale_shouldPassValue() {
        // Given
        String text = "Test";
        Double zeroLengthScale = 0.0;
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, null, zeroLengthScale, null, null);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(
                eq(text),
                any(),
                eq(0.0),
                any(),
                any()
        );
    }

    @Test
    void synthesize_withNegativeNoiseScale_shouldPassValue() {
        // Given - Piper might accept negative values
        String text = "Test";
        Double negativeNoiseScale = -0.5;
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, null, null, negativeNoiseScale, null);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(
                eq(text),
                any(),
                any(),
                eq(-0.5),
                any()
        );
    }

    @Test
    void synthesize_withVeryHighNoiseWScale_shouldPassValue() {
        // Given
        String text = "Test";
        Double highNoiseWScale = 5.0;
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        byte[] result = ttsService.synthesize(text, null, null, null, highNoiseWScale);

        // Then
        assertThat(result).isEqualTo(mockAudioData);

        verify(piperHttpClient).synthesize(
                eq(text),
                any(),
                any(),
                any(),
                eq(5.0)
        );
    }

    // ==================== Verification Tests ====================

    @Test
    void synthesize_shouldReturnSameAudioForSameInput() {
        // Given
        String text = "Consistent test";
        when(piperHttpClient.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        byte[] firstCall = ttsService.synthesize(text);
        byte[] secondCall = ttsService.synthesize(text);

        // Then
        assertThat(firstCall)
                .as("First call result")
                .isEqualTo(mockAudioData);

        assertThat(secondCall)
                .as("Second call result")
                .isEqualTo(mockAudioData)
                .isSameAs(firstCall);

        verify(piperHttpClient, times(2)).synthesize(eq(text), any(), any(), any(), any());
    }

}
