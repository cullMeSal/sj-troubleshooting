package sj.sj_troubleshooting.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sj.sj_troubleshooting.repository.UserRepository;
import sj.sj_troubleshooting.service.JwtUserDetailsService;
import sj.sj_troubleshooting.service.UserService;

import java.io.IOException;

@Component
public class AuthenticationRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Log when token is not found
        String tokenHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;


        // Check token
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            token = tokenHeader.substring(7);
            System.out.println("EMAIL IS: ");
            System.out.println(tokenManager.getEmailFromToken(token));
            try {
                email = tokenManager.getEmailFromToken(token); // ?
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else System.out.println("Bearer String not found in tokenAAAAAAAAAA");
        System.out.println("tokenHeader: " + tokenHeader);
        System.out.println("token: " + token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByEmail(email);
            if (tokenManager.validateJwtToken(token, userDetails)) { // ??? userDetails is found using
                // the username decoded from token. Then validateJwtToken() returns true if
                // username decoded from token param matches username from userDetails??
                UsernamePasswordAuthenticationToken
                        authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
