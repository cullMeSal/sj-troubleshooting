package sj.sj_troubleshooting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import sj.sj_troubleshooting.dto.UpdateUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.DeniedUserInfoRequestException;
import sj.sj_troubleshooting.exception.UserNotFoundException;
import sj.sj_troubleshooting.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUpdateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdateUserWithAsymmetricKeys_Success() {
        // Arrange
        Long userId = 1L;
        String userEmail = "test@example.com";
        
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("newUsername");
        updateDTO.setPassword("newPassword123");
        
        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");
        existingUser.setEmail(userEmail);
        
        UserEntity requestingUser = new UserEntity();
        requestingUser.setId(userId);
        requestingUser.setEmail(userEmail);
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(requestingUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);
        
        // Act
        UserEntity result = userService.updateUserWithAsymmetricKeys(userId, updateDTO, authentication);
        
        // Assert
        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertNotEquals("newPassword123", result.getPassword()); // Password should be encrypted
        verify(userRepository).save(any(UserEntity.class));
    }
    
    @Test
    void testUpdateUserWithAsymmetricKeys_UnauthorizedUser() {
        // Arrange
        Long userId = 1L;
        String userEmail = "test@example.com";
        String differentUserEmail = "different@example.com";
        
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("newUsername");
        
        UserEntity requestingUser = new UserEntity();
        requestingUser.setId(2L); // Different user ID
        requestingUser.setEmail(differentUserEmail);
        
        when(authentication.getName()).thenReturn(differentUserEmail);
        when(userRepository.findByEmail(differentUserEmail)).thenReturn(Optional.of(requestingUser));
        
        // Act & Assert
        assertThrows(DeniedUserInfoRequestException.class, () -> {
            userService.updateUserWithAsymmetricKeys(userId, updateDTO, authentication);
        });
    }
    
    @Test
    void testUpdateUserWithAsymmetricKeys_UserNotFound() {
        // Arrange
        Long userId = 1L;
        String userEmail = "test@example.com";
        
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("newUsername");
        
        UserEntity requestingUser = new UserEntity();
        requestingUser.setId(userId);
        requestingUser.setEmail(userEmail);
        
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(requestingUser));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserWithAsymmetricKeys(userId, updateDTO, authentication);
        });
    }
    
    @Test
    void testUpdateUserWithAsymmetricKeys_EmptyUpdateRequest() {
        // Arrange
        Long userId = 1L;
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        // No fields set - empty update request
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserWithAsymmetricKeys(userId, updateDTO, authentication);
        });
    }
}
