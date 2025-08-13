package sj.sj_troubleshooting.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {
    private static final long serialVersionUID = 123456L;

    @Value("${tokenValidity}")
    private int tokenValidity;

    @Autowired
    private RsaKeyManager rsaKeyManager;

    // generate Jwt token containing username from userDetails, stored in payload
    // combining with header and signature hashed using RS256 algo with private key
    public String generateJwtToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        System.out.println("UserDetails' username is: "+ userDetails.getUsername());
        System.out.println("UserDetails' password is: "+ userDetails.getPassword());
        return Jwts
                .builder()
                .setClaims(claims) // reset claims to an empty hashmap
                .setSubject(userDetails.getUsername()) // Username here is user's email
//                .claim("role", userDetails.getAuthorities().toArray())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256) // specify the algorithm to sign the jwt using private key
                .compact();
    }

    public Boolean validateJwtToken(String token, UserDetails userDetails){
        final String email = getEmailFromToken(token);
        final Claims claims = Jwts
                .parser()
                .verifyWith(getPublicKey()) // Use verifyWith instead of setSigningKey
                .build()
                .parseSignedClaims(token) // Use parseSignedClaims instead of parseClaimsJws
                .getPayload(); // Use getPayload instead of getBody
        // check the expiration state of token (redundant? because parser already check and throw exceptions if token expired)
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        // TRUE if token got matching email
        return (email.equals(userDetails.getUsername())) && !isTokenExpired;
    }

    public String getEmailFromToken(String token) {
        final Claims claims = Jwts
                .parser()
                .verifyWith(getPublicKey()) // Use verifyWith instead of setSigningKey
                .build()
                .parseSignedClaims(token) // Use parseSignedClaims instead of parseClaimsJws
                .getPayload(); // Use getPayload instead of getBody
        return claims.getSubject();
    }

    private PrivateKey getPrivateKey() {
        return rsaKeyManager.getPrivateKey();
    }

    private PublicKey getPublicKey() {
        return rsaKeyManager.getPublicKey();
    }
}