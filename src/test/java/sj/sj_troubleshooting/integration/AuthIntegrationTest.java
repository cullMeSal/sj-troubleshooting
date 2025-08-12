package sj.sj_troubleshooting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.repository.UserRepository;
import sj.sj_troubleshooting.service.UserService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        
        // Clean up test data
        userRepository.deleteAll();
    }

    @Test
    void testCompleteRegistrationFlow() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("integrationuser");
        registerDTO.setEmail("integration@example.com");
        registerDTO.setPassword("password123");

        // When & Then - Register
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.id").exists());

        // Verify user was saved in database
        assertTrue(userRepository.findByEmail("integration@example.com").isPresent());
    }

    @Test
    void testCompleteLoginFlow() throws Exception {
        // Given - First register a user
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("loginuser");
        registerDTO.setEmail("login@example.com");
        registerDTO.setPassword("password123");

        userService.registerNewUser(registerDTO);

        // When & Then - Login
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testRegistrationWithDuplicateEmail() throws Exception {
        // Given - First register a user
        RegisterNewUserDTO firstUser = new RegisterNewUserDTO();
        firstUser.setUsername("user1");
        firstUser.setEmail("duplicate@example.com");
        firstUser.setPassword("password123");

        userService.registerNewUser(firstUser);

        // When & Then - Try to register with same email
        RegisterNewUserDTO secondUser = new RegisterNewUserDTO();
        secondUser.setUsername("user2");
        secondUser.setEmail("duplicate@example.com");
        secondUser.setPassword("password456");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Entered email is already in use."));
    }

    @Test
    void testRegistrationWithInvalidEmail() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("invaliduser");
        registerDTO.setEmail("invalid-email");
        registerDTO.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email format"));
    }

    @Test
    void testLoginWithInvalidEmail() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email format"));
    }

    @Test
    void testLoginWithNonExistentUser() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        // Given - First register a user
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("wrongpassuser");
        registerDTO.setEmail("wrongpass@example.com");
        registerDTO.setPassword("password123");

        userService.registerNewUser(registerDTO);

        // When & Then - Login with wrong password
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("wrongpass@example.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegistrationWithEmptyFields() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("");
        registerDTO.setEmail("");
        registerDTO.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email format"));
    }

    @Test
    void testLoginWithEmptyFields() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("");
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email format"));
    }
}
