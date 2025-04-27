package com.github.nramc.dev.journey.api.core.app.health;

import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CloudinaryHealthIndicatorTest {
    private CloudinaryGateway cloudinaryService;
    private CloudinaryHealthIndicator cloudinaryHealthIndicator;

    @BeforeEach
    void setUp() {
        cloudinaryService = mock(CloudinaryGateway.class);
        cloudinaryHealthIndicator = new CloudinaryHealthIndicator(cloudinaryService);
    }

    @Test
    void health_whenServiceServiceAvailable_shouldProvideStatusUp() {
        when(cloudinaryService.isAvailable()).thenReturn(true);
        assertThat(cloudinaryHealthIndicator.health().getStatus().getCode()).isEqualTo("UP");
    }

    @Test
    void health_whenServiceServiceUnavailable_shouldProvideStatusDown() {
        when(cloudinaryService.isAvailable()).thenReturn(false);
        assertThat(cloudinaryHealthIndicator.health().getStatus().getCode()).isEqualTo("DOWN");
    }


}
