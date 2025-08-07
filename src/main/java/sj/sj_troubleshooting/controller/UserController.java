package sj.sj_troubleshooting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sj.sj_troubleshooting.dto.*;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/sayhi")
    public ResponseEntity<String> sayhi(){
        System.out.println("helo");
        return ResponseEntity.ok("henlo user");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable("id") Long id, Authentication authentication){
        return ResponseEntity.ok(userService.getUserInfo(id, authentication));
    }
    @GetMapping("/query")
    public ResponseEntity<?> queryUser(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "page", required = false) Integer page
    ){
        List<?> userList = userService.userQuery(limit, page, username, email);
        return ResponseEntity.ok(userList);
    }
    @PutMapping("/update")
    public ResponseEntity<?> modifyUser(@RequestBody UserEntity updatedUser){

        return ResponseEntity.ok(userService.updateUser(updatedUser));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> removeUser(@RequestBody String email){
        boolean deleted = userService.deleteUser(email);
        return ResponseEntity.ok(deleted ? "User deleted" : "User NOT deleted");
    }
}
