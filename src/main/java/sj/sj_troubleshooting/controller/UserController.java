package sj.sj_troubleshooting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sj.sj_troubleshooting.dto.*;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.DeniedUserInfoRequestException;
import sj.sj_troubleshooting.exception.UserNotFoundException;
import sj.sj_troubleshooting.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users/")
public class UserController {

    @Autowired
    UserService userService;

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

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateUserWithAsymmetricKeys(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserDTO updateUserDTO,
            Authentication authentication) {
        
        try {
            UserEntity updatedUser = userService.updateUserWithAsymmetricKeys(id, updateUserDTO, authentication);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (DeniedUserInfoRequestException e) {
            return ResponseEntity.status(403).body("Access denied: " + e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> removeUser(@RequestBody String email){
        boolean deleted = userService.deleteUser(email);
        return ResponseEntity.ok(deleted ? "User deleted" : "User NOT deleted");
    }
}
