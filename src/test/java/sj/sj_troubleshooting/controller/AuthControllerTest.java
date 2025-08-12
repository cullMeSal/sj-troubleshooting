package sj.sj_troubleshooting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");

        when(userService.registerNewUser(any(RegisterNewUserDTO.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testRegisterUser_InvalidInput() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        registerDTO.setUsername("");
        registerDTO.setEmail("invalid-email");
        registerDTO.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk()); // Controller doesn't validate, service does
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        JwtResponseModel response = new JwtResponseModel("jwt-token-here");

        when(userService.authenticateLogin(any(JwtRequestModel.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"));
    }

    @Test
    void testLogin_InvalidInput() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        loginRequest.setEmail("invalid-email");
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()); // Controller doesn't validate, service does
    }

    @Test
    void testRegisterUser_MissingFields() throws Exception {
        // Given
        RegisterNewUserDTO registerDTO = new RegisterNewUserDTO();
        // Missing all fields

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin_MissingFields() throws Exception {
        // Given
        JwtRequestModel loginRequest = new JwtRequestModel();
        // Missing all fields

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}
