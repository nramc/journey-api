package com.github.nramc.dev.journey.api.web.ai.narration;

import jakarta.validation.constraints.NotBlank;

public record NarrationEnhancerRequest(@NotBlank String narration, @NotBlank String tone) {
}
