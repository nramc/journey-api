package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images;

import com.github.nramc.dev.journey.api.config.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.NEW_JOURNEY_ENTITY;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateJourneyImagesDetailsResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class})
@ActiveProfiles({"prod", "test"})
class UpdateJourneyImagesDetailsResourceTest {
    private static final ResultMatcher[] STATUS_AND_CONTENT_TYPE_MATCH = new ResultMatcher[]{
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON)
    };
    private static final ResultMatcher[] JOURNEY_BASE_DETAILS_MATCH = new ResultMatcher[]{
            jsonPath("$.name").value("First Flight Experience"),
            jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
            jsonPath("$.tags").isArray(),
            jsonPath("$.tags").value(hasSize(3)),
            jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
            jsonPath("$.thumbnail").value("https://example.com/thumbnail.png"),
            jsonPath("$.journeyDate").value("2024-03-27"),
            jsonPath("$.createdDate").value("2024-03-27")
    };
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    JourneyRepository journeyRepository;
    @MockitoBean
    CloudinaryGateway cloudinaryGateway;

    @Test
    @WithMockAuthenticatedUser
    void updateImagesDetails_whenOptionalFieldDoesNotExists_thenShouldUpdateWithDefaultValues() throws Exception {
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.of(NEW_JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image"},
                 {"url":"image2.png", "assetId": "second-image"},
                 {"url":"image3.gif", "assetId": "third-image"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.imagesDetails.images[*].title").value(hasItems(nullValue(), nullValue(), nullValue())))
                .andExpect(jsonPath("$.imagesDetails.images[*].isFavorite").value(hasItems(false, false, false)))
                .andExpect(jsonPath("$.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithMockAuthenticatedUser
    void updateImagesDetails_whenOptionalFieldHaveValues_thenShouldUpdateWithGivenValues() throws Exception {
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.of(NEW_JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image", "title": "Image1 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image2.png", "assetId": "second-image", "title": "Image2 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image3.gif", "assetId": "third-image", "title": "Image3 Title", "isFavorite": true, "isThumbnail": false}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(JOURNEY_BASE_DETAILS_MATCH)
                .andExpect(jsonPath("$.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.imagesDetails.images[*].title").value(hasItems("Image1 Title", "Image2 Title", "Image3 Title")))
                .andExpect(jsonPath("$.imagesDetails.images[*].isFavorite").value(hasItems(true, true, true)))
                .andExpect(jsonPath("$.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithMockAuthenticatedUser
    void updateImagesDetails_whenIsThumbnailFlagEnabled_thenShouldUpdateThumbnailField() throws Exception {
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.of(NEW_JOURNEY_ENTITY));
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image", "title": "Image1 Title", "isFavorite": true, "isThumbnail": true},
                 {"url":"image2.png", "assetId": "second-image", "title": "Image2 Title", "isFavorite": true, "isThumbnail": false},
                 {"url":"image3.gif", "assetId": "third-image", "title": "Image3 Title", "isFavorite": true, "isThumbnail": false}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpectAll(STATUS_AND_CONTENT_TYPE_MATCH)
                .andExpectAll(
                        jsonPath("$.name").value("First Flight Experience"),
                        jsonPath("$.description").value("Travelled first time for work deputation to Germany, Munich city"),
                        jsonPath("$.tags").isArray(),
                        jsonPath("$.tags").value(hasSize(3)),
                        jsonPath("$.tags").value(hasItems("travel", "germany", "munich")),
                        jsonPath("$.thumbnail").value("image1.jpg"),
                        jsonPath("$.journeyDate").value("2024-03-27"),
                        jsonPath("$.createdDate").value("2024-03-27")
                )
                .andExpect(jsonPath("$.imagesDetails.images").value(hasSize(3)))
                .andExpect(jsonPath("$.imagesDetails.images[*].url").value(hasItems("image1.jpg", "image2.png", "image3.gif")))
                .andExpect(jsonPath("$.imagesDetails.images[*].assetId").value(hasItems("first-image", "second-image", "third-image")))
                .andExpect(jsonPath("$.imagesDetails.images[*].title").value(hasItems("Image1 Title", "Image2 Title", "Image3 Title")))
                .andExpect(jsonPath("$.imagesDetails.images[*].isFavorite").value(hasItems(true, true, true)))
                .andExpect(jsonPath("$.imagesDetails.images[*].isThumbnail").value(hasItems(false, false, false)));
    }

    @Test
    @WithAnonymousUser
    void updateImagesDetails_whenNotAuthenticated_shouldThrowError() throws Exception {
        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image"},
                 {"url":"image2.png", "assetId": "second-image"},
                 {"url":"image3.gif", "assetId": "third-image"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockGuestUser
    void updateImagesDetails_whenNotAuthorized_shouldThrowError() throws Exception {
        when(journeyRepository.findById(NEW_JOURNEY_ENTITY.getId())).thenReturn(Optional.empty());
        when(journeyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String jsonRequestTemplate = """
                { "images": [
                 {"url":"image1.jpg", "assetId": "first-image"},
                 {"url":"image2.png", "assetId": "second-image"},
                 {"url":"image3.gif", "assetId": "third-image"}
                ]
                }
                """;
        mockMvc.perform(put(Resources.UPDATE_JOURNEY, NEW_JOURNEY_ENTITY.getId())
                        .header(HttpHeaders.CONTENT_TYPE, Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS)
                        .content(jsonRequestTemplate)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
