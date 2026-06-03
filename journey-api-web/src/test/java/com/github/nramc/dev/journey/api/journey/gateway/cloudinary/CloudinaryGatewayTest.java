package com.github.nramc.dev.journey.api.journey.gateway.cloudinary;

import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.api.exceptions.NotFound;
import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.shared.exceptions.NonTechnicalException;
import com.github.nramc.dev.journey.api.shared.exceptions.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CloudinaryGatewayTest {

    private static final String JOURNEY_ID = "test-journey-id-123";
    private static final String ASSET_ID = "asset-abc-456";
    private static final String PUBLIC_ID_VALUE = "journeys/test-journey-id-123/image";
    private static final String ROOT_DIRECTORY = "journeys";

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Api api;

    @Mock
    private CloudinaryProperties cloudinaryProperties;

    private CloudinaryGateway cloudinaryGateway;

    private Journey testJourney;

    @BeforeEach
    void setUp() {
        cloudinaryGateway = new CloudinaryGateway(cloudinary, cloudinaryProperties);
        when(cloudinary.api()).thenReturn(api);

        testJourney = Journey.builder()
                .id(JOURNEY_ID)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // isAvailable
    // ─────────────────────────────────────────────────────────────────────────────
    @Nested
    class IsAvailableTests {

        @Test
        void isAvailable_whenPingSucceeds_shouldReturnTrue() throws Exception {
            ApiResponse pingResponse = mock(ApiResponse.class);
            when(api.ping(anyMap())).thenReturn(pingResponse);

            boolean result = cloudinaryGateway.isAvailable();

            assertThat(result).isTrue();
            verify(api).ping(anyMap());
        }

        @Test
        void isAvailable_whenPingThrowsException_shouldReturnFalse() throws Exception {
            when(api.ping(anyMap())).thenThrow(new RuntimeException("Cloudinary unavailable"));

            boolean result = cloudinaryGateway.isAvailable();

            assertThat(result).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // deleteImage
    // ─────────────────────────────────────────────────────────────────────────────
    @Nested
    class DeleteImageTests {

        @Test
        void deleteImage_whenSuccessful_shouldDeleteResourceByPublicId() throws Exception {
            ApiResponse resourceResponse = mock(ApiResponse.class);
            when(resourceResponse.getOrDefault("public_id", "")).thenReturn(PUBLIC_ID_VALUE);

            ApiResponse deleteResponse = mock(ApiResponse.class);
            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenReturn(resourceResponse);
            when(api.deleteResources(eq(Collections.singleton(PUBLIC_ID_VALUE)), anyMap())).thenReturn(deleteResponse);

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteImage(ASSET_ID));

            verify(api).resourceByAssetID(eq(ASSET_ID), anyMap());
            verify(api).deleteResources(eq(Collections.singleton(PUBLIC_ID_VALUE)), anyMap());
        }

        @Test
        void deleteImage_whenGetResourceThrowsException_shouldSwallowExceptionAndNotDelete() throws Exception {
            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenThrow(new RuntimeException("Resource not found"));

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteImage(ASSET_ID));

            verify(api, never()).deleteResources(any(), anyMap());
        }

        @Test
        @SuppressWarnings("unchecked")
        void deleteImage_whenDeleteResourcesThrowsException_shouldSwallowException() throws Exception {
            ApiResponse resourceResponse = mock(ApiResponse.class);
            when(resourceResponse.getOrDefault("public_id", "")).thenReturn(PUBLIC_ID_VALUE);

            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenReturn(resourceResponse);
            when(api.deleteResources(any(), anyMap())).thenThrow(new RuntimeException("Delete failed"));

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteImage(ASSET_ID));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // allResources
    // ─────────────────────────────────────────────────────────────────────────────
    @Nested
    class AllResourcesTests {

        @Test
        void allResources_whenResourcesExist_shouldReturnMappedCloudinaryAssets() throws Exception {
            Map<String, Object> resourceMap = new HashMap<>();
            resourceMap.put("url", "https://example.com/image.jpg");
            resourceMap.put("public_id", PUBLIC_ID_VALUE);
            resourceMap.put("asset_id", ASSET_ID);
            resourceMap.put("secure_url", "https://example.com/image.jpg");

            ApiResponse apiResponse = mock(ApiResponse.class);
            when(apiResponse.get("resources")).thenReturn(List.of(resourceMap));
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(apiResponse);

            List<CloudinaryAsset> result = cloudinaryGateway.allResources(testJourney);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().assetID()).isEqualTo(ASSET_ID);
            assertThat(result.getFirst().publicID()).isEqualTo(PUBLIC_ID_VALUE);
            assertThat(result.getFirst().url()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getFirst().securedUrl()).isEqualTo("https://example.com/image.jpg");
        }

        @Test
        void allResources_whenMultipleResourcesExist_shouldReturnAllMappedAssets() throws Exception {
            Map<String, Object> resource1 = Map.of(
                    "url", "https://example.com/image1.jpg",
                    "public_id", "public/image1",
                    "asset_id", "asset-001",
                    "secure_url", "https://example.com/image1.jpg"
            );
            Map<String, Object> resource2 = Map.of(
                    "url", "https://example.com/image2.jpg",
                    "public_id", "public/image2",
                    "asset_id", "asset-002",
                    "secure_url", "https://example.com/image2.jpg"
            );

            ApiResponse apiResponse = mock(ApiResponse.class);
            when(apiResponse.get("resources")).thenReturn(List.of(resource1, resource2));
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(apiResponse);

            List<CloudinaryAsset> result = cloudinaryGateway.allResources(testJourney);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(CloudinaryAsset::assetID).containsExactlyInAnyOrder("asset-001", "asset-002");
        }

        @Test
        void allResources_whenNoResourcesFound_shouldReturnEmptyList() throws Exception {
            ApiResponse apiResponse = mock(ApiResponse.class);
            when(apiResponse.get("resources")).thenReturn(Collections.emptyList());
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(apiResponse);

            List<CloudinaryAsset> result = cloudinaryGateway.allResources(testJourney);

            assertThat(result).isEmpty();
        }

        @Test
        void allResources_whenResourcesKeyIsMissing_shouldReturnEmptyList() throws Exception {
            ApiResponse apiResponse = mock(ApiResponse.class);
            when(apiResponse.get("resources")).thenReturn(null);
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(apiResponse);

            List<CloudinaryAsset> result = cloudinaryGateway.allResources(testJourney);

            assertThat(result).isEmpty();
        }

        @Test
        void allResources_whenApiThrowsException_shouldThrowTechnicalException() throws Exception {
            when(api.resourcesByContext(anyString(), anyString(), anyMap()))
                    .thenThrow(new RuntimeException("Cloudinary API error"));

            assertThatThrownBy(() -> cloudinaryGateway.allResources(testJourney))
                    .isInstanceOf(TechnicalException.class)
                    .hasMessageContaining("Unable to fetch all resources from Cloudinary");
        }

        @Test
        void allResources_whenResourceMapIsEmpty_shouldThrowTechnicalExceptionCausedByNonTechnicalException() throws Exception {
            ApiResponse apiResponse = mock(ApiResponse.class);
            when(apiResponse.get("resources")).thenReturn(List.of(new HashMap<>())); // empty map in list
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(apiResponse);

            // NonTechnicalException from toCloudinaryResource is caught by allResources and re-wrapped
            assertThatThrownBy(() -> cloudinaryGateway.allResources(testJourney))
                    .isInstanceOf(TechnicalException.class)
                    .hasMessageContaining("Unable to fetch all resources from Cloudinary")
                    .cause()
                    .isInstanceOf(NonTechnicalException.class)
                    .hasMessageContaining("Unable to fetch resources from Cloudinary");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // deleteJourney
    // ─────────────────────────────────────────────────────────────────────────────
    @Nested
    class DeleteJourneyTests {

        @Test
        void deleteJourney_whenJourneyHasNoImages_shouldDeleteEmptyFolderSuccessfully() throws Exception {
            ApiResponse listResponse = mock(ApiResponse.class);
            when(listResponse.get("resources")).thenReturn(Collections.emptyList());
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(listResponse);

            when(cloudinaryProperties.rootDirectory()).thenReturn(ROOT_DIRECTORY);
            when(api.deleteFolder(eq(ROOT_DIRECTORY + "/" + JOURNEY_ID), anyMap())).thenReturn(mock(ApiResponse.class));

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteJourney(testJourney));

            verify(api).deleteFolder(eq(ROOT_DIRECTORY + "/" + JOURNEY_ID), anyMap());
        }

        @Test
        @SuppressWarnings("unchecked")
        void deleteJourney_whenJourneyHasImages_shouldDeleteAllImagesAndFolder() throws Exception {
            Map<String, Object> resourceMap = new HashMap<>();
            resourceMap.put("url", "https://example.com/image.jpg");
            resourceMap.put("public_id", PUBLIC_ID_VALUE);
            resourceMap.put("asset_id", ASSET_ID);
            resourceMap.put("secure_url", "https://example.com/image.jpg");

            ApiResponse listResponse = mock(ApiResponse.class);
            when(listResponse.get("resources")).thenReturn(List.of(resourceMap));
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(listResponse);

            ApiResponse resourceResponse = mock(ApiResponse.class);
            when(resourceResponse.getOrDefault("public_id", "")).thenReturn(PUBLIC_ID_VALUE);
            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenReturn(resourceResponse);
            when(api.deleteResources(any(), anyMap())).thenReturn(mock(ApiResponse.class));

            when(cloudinaryProperties.rootDirectory()).thenReturn(ROOT_DIRECTORY);
            when(api.deleteFolder(anyString(), anyMap())).thenReturn(mock(ApiResponse.class));

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteJourney(testJourney));

            verify(api).deleteResources(eq(Collections.singleton(PUBLIC_ID_VALUE)), anyMap());
            verify(api).deleteFolder(eq(ROOT_DIRECTORY + "/" + JOURNEY_ID), anyMap());
        }

        @Test
        void deleteJourney_whenFolderNotFound_shouldIgnoreNotFoundAndComplete() throws Exception {
            ApiResponse listResponse = mock(ApiResponse.class);
            when(listResponse.get("resources")).thenReturn(Collections.emptyList());
            when(api.resourcesByContext(eq("id"), eq(JOURNEY_ID), anyMap())).thenReturn(listResponse);

            when(cloudinaryProperties.rootDirectory()).thenReturn(ROOT_DIRECTORY);
            when(api.deleteFolder(anyString(), anyMap())).thenThrow(new NotFound("Folder not found"));

            assertThatNoException().isThrownBy(() -> cloudinaryGateway.deleteJourney(testJourney));
        }

        @Test
        void deleteJourney_whenApiThrowsUnexpectedException_shouldThrowTechnicalException() throws Exception {
            when(api.resourcesByContext(anyString(), anyString(), anyMap()))
                    .thenThrow(new RuntimeException("Unexpected Cloudinary failure"));

            assertThatThrownBy(() -> cloudinaryGateway.deleteJourney(testJourney))
                    .isInstanceOf(TechnicalException.class)
                    .hasMessageContaining("Unable to delete journey resources");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // getResource
    // ─────────────────────────────────────────────────────────────────────────────
    @Nested
    class GetResourceTests {

        @Test
        void getResource_whenAssetExists_shouldReturnApiResponse() throws Exception {
            ApiResponse expectedResponse = mock(ApiResponse.class);
            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenReturn(expectedResponse);

            ApiResponse result = cloudinaryGateway.getResource(ASSET_ID);

            assertThat(result).isNotNull().isSameAs(expectedResponse);
            verify(api).resourceByAssetID(eq(ASSET_ID), anyMap());
        }

        @Test
        void getResource_whenApiThrowsException_shouldPropagateException() throws Exception {
            when(api.resourceByAssetID(eq(ASSET_ID), anyMap())).thenThrow(new RuntimeException("API error"));

            assertThatThrownBy(() -> cloudinaryGateway.getResource(ASSET_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("API error");
        }
    }
}



