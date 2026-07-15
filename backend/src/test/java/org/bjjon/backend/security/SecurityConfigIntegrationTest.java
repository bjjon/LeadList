package org.bjjon.backend.security;

import jakarta.servlet.http.Cookie;
import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class SecurityConfigIntegrationTest {

    private static final String RAW_PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;

    @AfterEach
    void cleanUp() {
        if (testUser != null) {
            userRepo.deleteById(testUser.getId());
            testUser = null;
        }
    }

    private String createAuthenticatedUserAndGetToken() {
        String email = "sec-config-" + UUID.randomUUID() + "@example.com";
        testUser = userRepo.save(User.builder()
                .firstname("Test")
                .lastname("User")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .build());
        String token = jwtService.generateToken(email);
        testUser.setToken(token);
        userRepo.save(testUser);
        return token;
    }

    @Test
    void loginEndpoint_isPermitAll_soItIsReachableWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().is(not(401)));
    }

    @Test
    void anyOtherEndpoint_requiresAuthentication_andReturns401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedRequest_isHandledByRestAuthenticationEntryPoint_withJsonErrorBody() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void jwtAuthenticationFilter_authenticatesValidBearerToken_soProtectedEndpointIsReachable() throws Exception {
        String token = createAuthenticatedUserAndGetToken();

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(not(401)));
    }

    @Test
    void csrf_isDisabled_soPostWithoutCsrfTokenIsNotBlocked() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().is(not(403)));
    }

    @Test
    void sessionManagement_isStateless_soNoSessionCookieIsCreated() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();
        boolean hasSessionCookie = Arrays.stream(cookies)
                        .anyMatch(c -> c.getName().equalsIgnoreCase("JSESSIONID"));
        assertThat(hasSessionCookie).isFalse();
    }
}
