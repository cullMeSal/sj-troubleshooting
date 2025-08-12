package sj.sj_troubleshooting.util;

import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;

public class TestDataBuilder {

    public static RegisterNewUserDTO createValidRegisterDTO() {
        RegisterNewUserDTO dto = new RegisterNewUserDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        return dto;
    }

    public static RegisterNewUserDTO createRegisterDTO(String username, String email, String password) {
        RegisterNewUserDTO dto = new RegisterNewUserDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    public static JwtRequestModel createValidLoginRequest() {
        JwtRequestModel request = new JwtRequestModel();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }

    public static JwtRequestModel createLoginRequest(String email, String password) {
        JwtRequestModel request = new JwtRequestModel();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    public static UserEntity createValidUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        return user;
    }

    public static UserEntity createUser(Long id, String username, String email, String password) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public static String[] getValidEmails() {
        return new String[]{
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@numbers.com",
                "user_name@domain.com",
                "user-name@domain.com"
        };
    }

    public static String[] getInvalidEmails() {
        return new String[]{
                "invalid-email",
                "@example.com",
                "test@",
                "test@.com",
                "test..test@example.com",
                "test@example..com",
                "test@example",
                ""
        };
    }
}
