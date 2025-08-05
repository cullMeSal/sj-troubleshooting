package sj.sj_troubleshooting.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sj.sj_troubleshooting.dto.JwtRequestModel;
import sj.sj_troubleshooting.dto.JwtResponseModel;
import sj.sj_troubleshooting.dto.RegisterNewUserDTO;
import sj.sj_troubleshooting.dto.UserQueryResultDTO;
import sj.sj_troubleshooting.entity.UserEntity;
import sj.sj_troubleshooting.exception.*;
import sj.sj_troubleshooting.repository.UserRepository;
import sj.sj_troubleshooting.security.TokenManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    @PersistenceContext
    EntityManager entityManager;

    public UserEntity registerNewUser(RegisterNewUserDTO userDTO) {
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

        return userRepo.save(user);
    }

    public JwtResponseModel authenticateLogin(JwtRequestModel request) {
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
        return new JwtResponseModel(jwtToken);
    }

    public UserEntity getUserInfo(Long id, Authentication authentication){
        System.out.println("Authentication: "+ authentication.getName());

        Optional<UserEntity> requestingUser = userRepo.findByEmail(authentication.getName());

//        Optional<UserEntity> foundUser = userRepo.findById(id);
        if (!(requestingUser.get().getId() == id)){
            throw new DeniedUserInfoRequestException("Error getting user info: User not found or You are unauthorized to get info of user with id: "+id);
        }
        return requestingUser.get();
    }

    public List<?> userQuery(Integer limit, Integer page, String username, String email){
        // Setup query
        System.out.println("Setting up query");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserQueryResultDTO> cq = cb.createQuery(UserQueryResultDTO.class);

        Root<UserEntity> user = cq.from(UserEntity.class);
        cq.select(cb.construct(
                UserQueryResultDTO.class,
                user.get("id"),
                user.get("username"),
                user.get("email")
        ));
        List<Predicate> predicates = new ArrayList<>();

        if (username != null) {predicates.add(cb.like(user.get("username"), "%" + username + "%"));}
        if (email != null) {predicates.add(cb.like(user.get("email"), "%" + email + "%"));}
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<UserQueryResultDTO> query = entityManager.createQuery(cq);
        List<UserQueryResultDTO> resultList = query.getResultList();
        resultList.sort(Comparator.comparingLong(UserQueryResultDTO::getId));

        System.out.println("Setting up pagination");
        // Pagination
        Integer total = resultList.size();

        // Just page number - ERROR
        if (limit == null && page != null) throw new InvalidUserQueryRequestException(
                "Invalid Query Request: 'page' param can't be used without 'limit' param.");

        int effectivePage = (page != null) ? page : 1;
        int effectiveLimit = (limit != null) ? limit : total;

        if (effectiveLimit <= 0 || effectivePage <= 0) {
            throw new NonPositiveInputException("Page limit and Page number must be positive.");
        }

        int start = (effectivePage - 1) * effectiveLimit;
        if (start >= total) {
            throw new UserQueryOutOfBoundException("Queried list is beyond result list size: " + total);
        }

        int end = Math.min(start + effectiveLimit, total);

        // Build and return the paginated response
        List<UserQueryResultDTO> retList = new ArrayList<>();
        retList.add(new UserQueryResultDTO(effectivePage, effectiveLimit, total));
        retList.addAll(resultList.subList(start, end));
        return retList;
    }

    public UserEntity updateUser(UserEntity updatedUser){
        System.out.println("Raw password: "+updatedUser.getPassword());
        updatedUser.setPassword(new BCryptPasswordEncoder(4).encode(updatedUser.getPassword()));
        System.out.println("Encrypted password: "+updatedUser.getPassword());
//        return userRepo.save(updatedUser);
        return userRepo.save(updatedUser);
    }

    public boolean deleteUser(String email){
        Optional<UserEntity> userToDelete = userRepo.findByEmail(email);
        if (userToDelete.isEmpty()) {
            System.out.println("User not found");
            return false;
        }
        userRepo.delete(userToDelete.get());
        return true;
    }

    protected boolean checkEmailValidity(String email) {
        return email.matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$");
    }
}
