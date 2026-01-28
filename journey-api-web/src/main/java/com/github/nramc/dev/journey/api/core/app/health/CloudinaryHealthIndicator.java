package com.github.nramc.dev.journey.api.core.app.health;

import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

@RequiredArgsConstructor
public class CloudinaryHealthIndicator implements HealthIndicator {
    private final CloudinaryGateway cloudinaryService;

    @Override
    public Health health() {
        return cloudinaryService.isAvailable() ? Health.up().build() : Health.down().build();
    }
}
