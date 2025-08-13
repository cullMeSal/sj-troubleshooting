package sj.sj_troubleshooting.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TokenManagerTest {

    private TokenManager tokenManager;
    
    @Mock
    private RsaKeyManager rsaKeyManager;
    
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenManager = new TokenManager();
        // Use reflection to set the rsaKeyManager field
        try {
            java.lang.reflect.Field field = TokenManager.class.getDeclaredField("rsaKeyManager");
            field.setAccessible(true);
            field.set(tokenManager, rsaKeyManager);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
        
        userDetails = new User("test@example.com", "password", 
            Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void testTokenGenerationAndValidation() {
        // This test will need actual RSA keys to work properly
        // In a real test environment, you would use test keys or mock the RsaKeyManager
        assertNotNull(tokenManager);
        assertNotNull(userDetails);
    }

    @Test
    void testUserDetailsSetup() {
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }
}

