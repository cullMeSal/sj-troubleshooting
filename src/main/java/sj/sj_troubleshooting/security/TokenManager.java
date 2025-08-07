package sj.sj_troubleshooting.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sj.sj_troubleshooting.utils.KeyLoader;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Component
public class TokenManager {
    private static final long serialVersionUID = 123456L;

    @Value("${secret}")
    private String jwtSecret; // get value from application.property

    @Value("${tokenValidity}")
    private int tokenValidity;

    private RSAPrivateKey privateKey = KeyLoader.loadPrivateKey("/src/main/resources/keys/private_key.pem");
    private RSAPublicKey publicKey = KeyLoader.loadPublicKey("/src/main/resources/keys/public_key.pem");
    public TokenManager() throws Exception {
    }

    // generate Jwt token containing username from userDetails, stored in payload
    // combining with header and signature hashed using HS256 algo
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
                .signWith(publicKey, SignatureAlgorithm.HS256) // specify the algorithm to sign the jwt using specific key
                .compact();
    }

    public Boolean validateJwtToken(String token, UserDetails userDetails){
        final String email = getEmailFromToken(token);
        final Claims claims = Jwts
                .parser() // initiate parser
                .setSigningKey(privateKey) // set the key for the parser
                .build() // build the will-be-immutable parser
                .parseClaimsJws(token).getBody(); // use parser to decrypt token to get claim in the payload
        // check the expiration state of token (redundant? because parser already check and throw exceptions if token expired)
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        // TRUE if token got matching email
        return (email.equals(userDetails.getUsername())) && !isTokenExpired;
    }

    public String getEmailFromToken(String token) {
        final Claims claims = Jwts
                .parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes); // generate symmetrical key
        return key;
    }
}