package com.github.nramc.dev.journey.api.web.resources.rest.ai.narration;

import jakarta.validation.constraints.NotBlank;

public record NarrationEnhancerRequest(@NotBlank String narration, @NotBlank String tone) {
}
