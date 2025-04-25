package com.github.nramc.dev.journey.api.core.app.health;

import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

@RequiredArgsConstructor
public class CloudinaryHealthIndicator implements HealthIndicator {
    private final CloudinaryGateway cloudinaryService;

    @Override
    public Health health() {
        return cloudinaryService.isAvailable() ? Health.up().build() : Health.down().build();
    }
}
