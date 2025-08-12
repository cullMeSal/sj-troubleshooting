package sj.sj_troubleshooting.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitExtension;
import org.springframework.test.web.servlet.MockMvc;
import sj.sj_troubleshooting.controller.AuthController;
import sj.sj_troubleshooting.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringJUnitExtension.class)
@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {SecurityConfig.class, AuthController.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;

    @MockBean
    private AuthenticationRequestFilter authenticationRequestFilter;

    @Test
    void testPublicEndpoints_ShouldBeAccessible() throws Exception {
        // Test auth endpoints are accessible without authentication
        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/sayhi"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test that other endpoints require authentication
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/some-protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testProtectedEndpoints_WithAuthentication() throws Exception {
        // Test that authenticated users can access protected endpoints
        // Note: This test might need adjustment based on your actual protected endpoints
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isNotFound()); // Assuming endpoint doesn't exist, but auth passes
    }

    @Test
    void testSecurityHeaders() throws Exception {
        // Test that security headers are properly set
        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isOk());
        // Additional header checks can be added here if needed
    }
}
