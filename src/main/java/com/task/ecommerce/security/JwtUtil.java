package com.task.ecommerce.security;

import com.task.ecommerce.entity.User;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("jti", UUID.randomUUID().toString());
        return createToken(claims, user.getId());
    }

    private String createToken(Map<String, Object> claims, Integer userId) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey(){
        byte[] KeyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
           final Claims claims = extractAllClaims(token);
           return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String userId = extractUsername(token);

        return userId.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public UUID extractJti(String token) {
        return UUID.fromString(
                extractClaim(token, claims -> claims.get("jti", String.class))
        );
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
