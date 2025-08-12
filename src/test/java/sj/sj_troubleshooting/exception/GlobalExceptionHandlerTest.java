package sj.sj_troubleshooting.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void testHandleEmailUnavailableException() {
        // Given
        EmailUnavailableException exception = new EmailUnavailableException("Email already exists");

        // When
        ResponseEntity<String> response = exceptionHandler.handleEmailUnavailableException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    void testHandleInvalidEmailFormatException() {
        // Given
        InvalidEmailFormatException exception = new InvalidEmailFormatException("Invalid email format");

        // When
        ResponseEntity<String> response = exceptionHandler.handleInvalidEmailFormatException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email format", response.getBody());
    }

    @Test
    void testHandleInvalidUserQueryRequestException() {
        // Given
        InvalidUserQueryRequestException exception = new InvalidUserQueryRequestException("Invalid query request");

        // When
        ResponseEntity<String> response = exceptionHandler.handleInvalidUserQueryRequestException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid query request", response.getBody());
    }

    @Test
    void testHandleNonPositiveInputException() {
        // Given
        NonPositiveInputException exception = new NonPositiveInputException("Input must be positive");

        // When
        ResponseEntity<String> response = exceptionHandler.handleNonPositiveInputException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Input must be positive", response.getBody());
    }

    @Test
    void testHandleUserQueryOutOfBoundException() {
        // Given
        UserQueryOutOfBoundException exception = new UserQueryOutOfBoundException("Query out of bounds");

        // When
        ResponseEntity<String> response = exceptionHandler.handleUserQueryOutOfBoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Query out of bounds", response.getBody());
    }

    @Test
    void testHandleDeniedUserInfoRequestException() {
        // Given
        DeniedUserInfoRequestException exception = new DeniedUserInfoRequestException("Access denied");

        // When
        ResponseEntity<String> response = exceptionHandler.handleDeniedUserInfoRequestException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }

    @Test
    void testHandleGenericException() {
        // Given
        RuntimeException exception = new RuntimeException("Generic error");

        // When
        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody());
    }

    @Test
    void testHandleAuthenticationException() {
        // Given
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        // When
        ResponseEntity<String> response = exceptionHandler.handleAuthenticationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody());
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        // Given
        RuntimeException exception = new RuntimeException(null);

        // When
        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

    @Test
    void testHandleExceptionWithEmptyMessage() {
        // Given
        RuntimeException exception = new RuntimeException("");

        // When
        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
