package sj.sj_troubleshooting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sj.sj_troubleshooting.dto.AuthenticateUserDTO;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/sayhi")
    public ResponseEntity<String> sayhi(){
        System.out.println("helo");
        return ResponseEntity.ok("henlo user");
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterNewUserDTO registerDTO){
        return userService.registerNewUser(registerDTO);
    }
    @PostMapping("/auth")
    public ResponseEntity<JwtResponseModel> authenticate(
            @RequestBody JwtRequestModel request
            ) {
        return userService.authenticateLogin(request);
    }
}
