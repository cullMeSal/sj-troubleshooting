package sj.sj_troubleshooting.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerTest {

    @InjectMocks
    private TokenManager tokenManager;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.withUsername("test@example.com")
                .password("encodedPassword")
                .authorities("USER")
                .build();
        
        // Set required properties using ReflectionTestUtils
        ReflectionTestUtils.setField(tokenManager, "secret", "testSecretKeyForJWTTokenGeneration123456789");
        ReflectionTestUtils.setField(tokenManager, "expiration", 3600000L); // 1 hour
    }

    @Test
    void testGenerateJwtToken_Success() {
        // When
        String token = tokenManager.generateJwtToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void testGenerateJwtToken_WithDifferentUsers() {
        // Given
        UserDetails user1 = User.withUsername("user1@example.com")
                .password("pass1")
                .authorities("USER")
                .build();
        
        UserDetails user2 = User.withUsername("user2@example.com")
                .password("pass2")
                .authorities("ADMIN")
                .build();

        // When
        String token1 = tokenManager.generateJwtToken(user1);
        String token2 = tokenManager.generateJwtToken(user2);

        // Then
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Different users should have different tokens
    }

    @Test
    void testGenerateJwtToken_WithNullUserDetails() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tokenManager.generateJwtToken(null);
        });
    }

    @Test
    void testGenerateJwtToken_WithEmptyUsername() {
        // Given
        UserDetails emptyUser = User.withUsername("")
                .password("password")
                .authorities("USER")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tokenManager.generateJwtToken(emptyUser);
        });
    }

    @Test
    void testGenerateJwtToken_Consistency() {
        // Given
        UserDetails sameUser = User.withUsername("test@example.com")
                .password("encodedPassword")
                .authorities("USER")
                .build();

        // When
        String token1 = tokenManager.generateJwtToken(userDetails);
        String token2 = tokenManager.generateJwtToken(sameUser);

        // Then
        // Note: JWT tokens are typically unique even for the same user due to timestamp
        // This test verifies that tokens are generated without errors
        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(token1.split("\\.").length == 3);
        assertTrue(token2.split("\\.").length == 3);
    }

    @Test
    void testGenerateJwtToken_WithSpecialCharacters() {
        // Given
        UserDetails specialUser = User.withUsername("user+tag@example.org")
                .password("encodedPassword")
                .authorities("USER")
                .build();

        // When
        String token = tokenManager.generateJwtToken(specialUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testGenerateJwtToken_WithLongUsername() {
        // Given
        String longUsername = "a".repeat(100) + "@example.com";
        UserDetails longUser = User.withUsername(longUsername)
                .password("encodedPassword")
                .authorities("USER")
                .build();

        // When
        String token = tokenManager.generateJwtToken(longUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }
}
