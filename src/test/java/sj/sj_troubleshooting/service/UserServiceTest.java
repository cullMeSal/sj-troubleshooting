package sj.sj_troubleshooting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.EmailUnavailableException;
import sj.sj_troubleshooting.exception.InvalidEmailFormatException;
import sj.sj_troubleshooting.repository.UserRepository;
import sj.sj_troubleshooting.security.TokenManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepo;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @InjectMocks
    private UserService userService;

    private RegisterNewUserDTO validRegisterDTO;
    private JwtRequestModel validLoginRequest;
    private UserEntity testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        validRegisterDTO = new RegisterNewUserDTO();
        validRegisterDTO.setUsername("testuser");
        validRegisterDTO.setEmail("test@example.com");
        validRegisterDTO.setPassword("password123");

        validLoginRequest = new JwtRequestModel();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");

        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        testUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encodedPassword")
                .authorities("USER")
                .build();
    }

    @Test
    void testRegisterNewUser_Success() {
        // Given
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity result = userService.registerNewUser(validRegisterDTO);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertTrue(result.getPassword().startsWith("$2a$")); // BCrypt encoded
        verify(userRepo).findByEmail("test@example.com");
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void testRegisterNewUser_EmailAlreadyExists() {
        // Given
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(EmailUnavailableException.class, () -> {
            userService.registerNewUser(validRegisterDTO);
        });
        verify(userRepo).findByEmail("test@example.com");
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterNewUser_InvalidEmailFormat() {
        // Given
        validRegisterDTO.setEmail("invalid-email");
        when(userRepo.findByEmail("invalid-email")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidEmailFormatException.class, () -> {
            userService.registerNewUser(validRegisterDTO);
        });
        verify(userRepo).findByEmail("invalid-email");
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterNewUser_ValidEmailFormats() {
        // Given
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@numbers.com"
        };

        for (String email : validEmails) {
            validRegisterDTO.setEmail(email);
            when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
            when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);

            // When
            UserEntity result = userService.registerNewUser(validRegisterDTO);

            // Then
            assertNotNull(result);
            assertEquals(email, result.getEmail());
        }
    }

    @Test
    void testAuthenticateLogin_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtUserDetailsService.loadUserByEmail("test@example.com")).thenReturn(testUserDetails);
        when(tokenManager.generateJwtToken(testUserDetails)).thenReturn("jwt-token-here");

        // When
        JwtResponseModel result = userService.authenticateLogin(validLoginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token-here", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUserDetailsService).loadUserByEmail("test@example.com");
        verify(tokenManager).generateJwtToken(testUserDetails);
    }

    @Test
    void testAuthenticateLogin_InvalidEmailFormat() {
        // Given
        validLoginRequest.setEmail("invalid-email");

        // When & Then
        assertThrows(InvalidEmailFormatException.class, () -> {
            userService.authenticateLogin(validLoginRequest);
        });
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateLogin_BadCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.authenticateLogin(validLoginRequest);
        });
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUserDetailsService, never()).loadUserByEmail(anyString());
    }

    @Test
    void testAuthenticateLogin_UserDisabled() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("User disabled"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.authenticateLogin(validLoginRequest);
        });
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUserDetailsService, never()).loadUserByEmail(anyString());
    }

    @Test
    void testCheckEmailValidity_ValidEmails() {
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@numbers.com",
                "user_name@domain.com",
                "user-name@domain.com"
        };

        for (String email : validEmails) {
            assertTrue(userService.checkEmailValidity(email), "Email should be valid: " + email);
        }
    }

    @Test
    void testCheckEmailValidity_InvalidEmails() {
        String[] invalidEmails = {
                "invalid-email",
                "@example.com",
                "test@",
                "test@.com",
                "test..test@example.com",
                "test@example..com",
                "test@example",
                ""
        };

        for (String email : invalidEmails) {
            assertFalse(userService.checkEmailValidity(email), "Email should be invalid: " + email);
        }
    }

    @Test
    void testPasswordEncryption() {
        // Given
        String originalPassword = "password123";
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        userService.registerNewUser(validRegisterDTO);

        // Then
        verify(userRepo).save(argThat(user -> {
            String savedPassword = user.getPassword();
            return savedPassword.startsWith("$2a$") && 
                   !savedPassword.equals(originalPassword) &&
                   new BCryptPasswordEncoder(4).matches(originalPassword, savedPassword);
        }));
    }
}
