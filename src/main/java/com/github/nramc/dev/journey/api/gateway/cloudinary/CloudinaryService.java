package com.github.nramc.dev.journey.api.gateway.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.api.exceptions.NotFound;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.core.exceptions.NonTechnicalException;
import com.github.nramc.dev.journey.api.core.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private static final String PUBLIC_ID = "public_id";
    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    // BEGIN-NOSCAN
    public void deleteImage(String assetID) {
        try {
            ApiResponse resourceResponse = getResource(assetID);

            ApiResponse apiResponse = cloudinary.api().deleteResources(Collections.singleton(getPublicId(resourceResponse)), Map.of());
            log.info("Response: {}", apiResponse);
        } catch (Exception ex) {
            log.warn("Failed to delete image", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CloudinaryResourceRepresentation> allResources(Journey journey) {
        try {
            ApiResponse apiResponse = cloudinary.api().resourcesByContext("id", journey.id(), Map.of());
            log.info("allResources: {}", apiResponse);
            List<Map<String, Object>> resources = (List<Map<String, Object>>) apiResponse.get("resources");
            return CollectionUtils.emptyIfNull(resources).stream().map(this::toCloudinaryResource).toList();
        } catch (Exception ex) {
            throw new TechnicalException("Unable to fetch all resources from Cloudinary", ex);
        }
    }

    private CloudinaryResourceRepresentation toCloudinaryResource(Map<String, Object> resources) {
        if (resources != null && !resources.isEmpty()) {
            return CloudinaryResourceRepresentation.builder()
                    .url(resources.get("url").toString())
                    .publicID(resources.get(PUBLIC_ID).toString())
                    .assetID(resources.get("asset_id").toString())
                    .securedUrl(resources.get("secure_url").toString())
                    .build();
        }
        throw new NonTechnicalException("Unable to fetch resources from Cloudinary");
    }

    public void deleteJourney(Journey journey) {
        try {
            deleteAllResources(journey);
            deleteDirectory(journey);
        } catch (Exception ex) {
            throw new TechnicalException("Unable to delete journey resources", ex);
        }
    }

    private void deleteAllResources(Journey journey) {
        List<CloudinaryResourceRepresentation> resources = allResources(journey);
        CollectionUtils.emptyIfNull(resources).forEach(resource -> deleteImage(resource.assetID()));
    }

    private void deleteDirectory(Journey journey) throws Exception {
        try {
            String journeyDirectory = String.join("/", cloudinaryProperties.rootDirectory(), journey.id());
            cloudinary.api().deleteFolder(journeyDirectory, Map.of());
        } catch (NotFound notFound) {
            log.warn("Journey directory not found: {}", journey.id());
        }
    }

    public ApiResponse getResource(String assetID) throws Exception {
        return cloudinary.api().resourceByAssetID(assetID, Map.of());
    }

    @SuppressWarnings("unchecked")
    private String getPublicId(ApiResponse apiResponse) {
        return (String) apiResponse.getOrDefault(PUBLIC_ID, "");

    }
    // END-NOSCAN
}
