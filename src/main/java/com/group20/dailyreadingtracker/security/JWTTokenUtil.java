package com.group20.dailyreadingtracker.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Utility class for JSON Web Token (JWT) generation, validation, and processing operations.
 * 
 * This component provides:
 * - JWT generation with configurable expiration time
 * - Token validation against the secret key
 * - Subject extraction from valid tokens
 * - Secure token signing using HMAC-SHA256 algorithm
 * 
 * The service enforces security practices including:
 * - Mandatory secret key configuration
 * - Strict token expiration validation
 * - Proper exception handling during token parsing
 * - Secure signature verification
 * 
 * @author Tianyi Wu
 * @author Sofiia Mamonova
 */

 @Component
 public class JWTTokenUtil {
     
     @Value("${jwt.secret}")
     private String secretString;
     
     @Value("${jwt.expirationMs}")
     private long expirationMs;
     
     private SecretKey secretKey;
 
     @PostConstruct
     public void init() {
         this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
     }
 
     public String generateToken(String subject) {
         return Jwts.builder()
                 .subject(subject)
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis() + expirationMs))
                 .signWith(secretKey, Jwts.SIG.HS256)
                 .compact();
     }
 
     public boolean validateToken(String token) {
         try {
             Jwts.parser()
                 .verifyWith(secretKey)
                 .build()
                 .parseSignedClaims(token);

             return true;
         } catch (Exception e) {
             return false;
         }
     }
 
     public String getSubject(String token) {
         return Jwts.parser()
                 .verifyWith(secretKey)
                 .build()
                 .parseSignedClaims(token)
                 .getPayload()
                 .getSubject();
     }
 }