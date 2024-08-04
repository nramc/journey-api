package com.github.nramc.dev.journey.api.gateway.cloudinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
public record CloudinaryResource(
        @JsonProperty("asset_id") String assetID,
        @JsonProperty("public_id") String publicID,
        @JsonProperty("url") String url,
        @JsonProperty("secure_url") String securedUrl
) {
}
