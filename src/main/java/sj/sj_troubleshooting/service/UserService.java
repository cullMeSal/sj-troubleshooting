package sj.sj_troubleshooting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.InvalidEmailFormatException;
import sj.sj_troubleshooting.exception.EmailUnavailableException;
import sj.sj_troubleshooting.repository.UserRepository;
import sj.sj_troubleshooting.security.TokenManager;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TokenManager tokenManager;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    public ResponseEntity<?> registerNewUser(RegisterNewUserDTO userDTO) {
        if (userRepo.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailUnavailableException("Entered email is already in use.");
        }
        if (!checkEmailValidity(userDTO.getEmail())) {
            throw new InvalidEmailFormatException("Invalid email format");
        }
        UserEntity user = new UserEntity();
        user.setUsername(userDTO.getUsername());
        user.setPassword(new BCryptPasswordEncoder(4).encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        return ResponseEntity.ok(userRepo.save(user));
    }

    public ResponseEntity<JwtResponseModel> authenticateLogin(JwtRequestModel request) {
        if (!checkEmailValidity(request.getEmail()))
            throw new InvalidEmailFormatException("Invalid email format");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByEmail(request.getEmail());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }


    protected boolean checkEmailValidity(String email) {
        return email.matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$");
    }
}
