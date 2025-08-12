package sj.sj_troubleshooting.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    private UsernamePasswordAuthenticationToken authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken("test@example.com", "password123");
        
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encodedPassword")
                .authorities("USER")
                .build();
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // When
        Authentication result = authenticationProvider.authenticate(authentication);

        // Then
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("test@example.com", result.getName());
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void testAuthenticate_BadCredentials() {
        // Given
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthenticate_NullCredentials() {
        // Given
        authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthenticate_EmptyCredentials() {
        // Given
        authentication = new UsernamePasswordAuthenticationToken("test@example.com", "");

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(authentication);
        });
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testSupports_UsernamePasswordAuthenticationToken() {
        // Given
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "pass");

        // When
        boolean result = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // Then
        assertTrue(result);
    }

    @Test
    void testSupports_OtherAuthenticationType() {
        // Given
        Authentication otherAuth = mock(Authentication.class);

        // When
        boolean result = authenticationProvider.supports(otherAuth.getClass());

        // Then
        assertFalse(result);
    }
}
