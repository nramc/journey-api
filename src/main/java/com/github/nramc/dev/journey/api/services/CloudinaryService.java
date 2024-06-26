package com.github.nramc.dev.journey.api.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private static final String PUBLIC_ID = "public_id";
    private final Cloudinary cloudinary;

    public void deleteImage(String assetID) {
        try {
            ApiResponse resourceResponse = getResource(assetID);

            ApiResponse apiResponse = cloudinary.api().deleteResources(Collections.singleton(getPublicId(resourceResponse)), Map.of());
            log.info("Response: {}", apiResponse);
        } catch (Exception ex) {
            log.warn("Failed to delete image", ex);
        }
    }

    public ApiResponse getResource(String assetID) throws Exception {
        return cloudinary.api().resourceByAssetID(assetID, Map.of());
    }

    @SuppressWarnings("unchecked")
    private String getPublicId(ApiResponse apiResponse) {
        return (String) apiResponse.getOrDefault(PUBLIC_ID, "");

    }
}
