package com.proj.slotify.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long jwtExpiryMs;

    public JwtUtil(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_EXPIRY_MS:86400000}") Long jwtExpiryMs
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiryMs = jwtExpiryMs;
    }

    public String generateToken(String userId, String email){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiryMs);

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
