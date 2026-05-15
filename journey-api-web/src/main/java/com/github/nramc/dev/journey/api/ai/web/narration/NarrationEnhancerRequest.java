package com.github.nramc.dev.journey.api.ai.web.narration;

import jakarta.validation.constraints.NotBlank;

public record NarrationEnhancerRequest(@NotBlank String narration, @NotBlank String tone) {
}
