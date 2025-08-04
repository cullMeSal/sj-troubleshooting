package sj.sj_troubleshooting.dto;

import lombok.Data;

@Data
public class AuthenticateUserDTO {
    String email;
    String password;
}
