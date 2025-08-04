package sj.sj_troubleshooting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.InvalidEmailFormatException;
import sj.sj_troubleshooting.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!checkEmailValidity(email))
            throw new InvalidEmailFormatException("Invalid email format");

        Optional<UserEntity> foundUser = userRepo.findByEmail(email);
//        ArrayList<String> role = new ArrayList<>();
//        role.add("user");
        if (foundUser.isPresent()) {
            return new User(
                    foundUser.get().getEmail(),
                    foundUser.get().getPassword(),
                    new ArrayList<>()
            );
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }

    public UserDetails loadUserByEmail(String email) {
        if (!checkEmailValidity(email))
            throw new InvalidEmailFormatException("Invalid email format");

        Optional<UserEntity> foundUser = userRepo.findByEmail(email);
//        ArrayList<String> role = new ArrayList<>();
//        role.add("user");
        if (foundUser.isPresent()) {
            return new User(
                    foundUser.get().getEmail(),
                    foundUser.get().getPassword(),
                    new ArrayList<>()
            );
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
    protected boolean checkEmailValidity(String email) {
        return email.matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$");
    }
}
