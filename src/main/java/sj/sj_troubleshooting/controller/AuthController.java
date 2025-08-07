package sj.sj_troubleshooting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(
            @RequestBody RegisterNewUserDTO registerDTO){
        return ResponseEntity.ok(userService.registerNewUser(registerDTO));
    }
    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> authenticate(
            @RequestBody JwtRequestModel request
    ) {
        return ResponseEntity.ok(userService.authenticateLogin(request));
    }
}
