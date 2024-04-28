package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.nramc.dev.journey.api.security.Roles.Constants.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.GUEST;
import static com.github.nramc.dev.journey.api.security.Roles.Constants.MAINTAINER;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_MY_ACCOUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class UpdateUserResourceTest {
    private static final String REQUEST_TEMPLATE = """
            {
             "name": "%s",
             "emailAddress": "%s"
            }""";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(27017);

    @Test
    void testContext() {
        assertNotNull(mockMvc);
    }


    @Test
    void change_whenUserNotAuthenticated_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("Valid Name", "valid-email-addresss@gmail.com"))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {GUEST})
    void change_whenUserNotAuthorized_thenShouldThrowError() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("Valid Name", "valid-email-addresss@gmail.com"))
                ).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {AUTHENTICATED_USER})
    void change_whenUserAuthenticatedUser_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {MAINTAINER})
    void change_whenUserMaintainer_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {ADMINISTRATOR})
    void change_whenUserAdministrator_thenCanUpdateDetails() throws Exception {
        updateAccountDetails();
    }

    private void updateAccountDetails() throws Exception {
        mockMvc.perform(post(UPDATE_MY_ACCOUNT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_TEMPLATE.formatted("Updated Valid Name", "updated-valid-email-addresss@gmail.com"))
                ).andDo(print())
                .andExpect(status().isOk());
        AuthUser user = userRepository.findUserByUsername("admin");
        assertEquals("Updated Valid Name", user.getName());
        assertEquals("updated-valid-email-addresss@gmail.com", user.getEmailAddress());
    }

}