package org.bjjon.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class JwtServiceTest {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, expiration);
    }

    @Test
    void generateToken_shouldReturnNonNullAndNonBlankToken() {
        String token = jwtService.generateToken("test@example.com");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_shouldReturnValidJwtFormat() {
        String token = jwtService.generateToken("test@example.com");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractEmail_shouldExtractEmailFromToken() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("test@example.com");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        String token = jwtService.generateToken("test@example.com");
        String invalidToken = token.substring(0, token.length() - 2) + "xx";
        assertFalse(jwtService.validateToken(invalidToken));
    }
}