package com.group20.dailyreadingtracker.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group20.dailyreadingtracker.security.JWTTokenUtil;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;
import com.group20.dailyreadingtracker.utils.EmailService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service handling email verification token operations.
 * 
 * Manages:
 * - Token generation and validation
 * - Email verification workflows
 * - User account activation
 * 
 * Key Features:
 * - JWT-based token generation
 * - Expiration handling (24hr TTL)
 * - Verification status tracking
 * - Automated email delivery
 * 
 * @author Sofiia Mamonova
 * @author Tianyi Wu
 */

@Service
public class VerificationTokenService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final JWTTokenUtil jwtTokenUtil;
    
    private static final Logger logger = LoggerFactory.getLogger(VerificationTokenService.class);

    public VerificationTokenService(UserRepository userRepository, 
                                    VerificationTokenRepository verificationTokenRepository, 
                                    EmailService emailService, JWTTokenUtil jwtTokenUtil){
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    public void createVerificationForRegisteredUser(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        verificationTokenRepository.deleteAllByUser(user);

        VerificationToken token = new VerificationToken();
        token.setToken(jwtTokenUtil.generateToken(email));
        token.setUser(user);
        token.setStatus(VerificationToken.STATUS_PENDING);
        token.setExpiredDateTime(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(token);

        String verificationUrl = generateVerificationUrl(request, token.getToken());

        emailService.sendVerificationEmail(user, verificationUrl); 
    }

    @Transactional
    public ResponseEntity<String> verifyEmail(String tokenValue) {
        try {
            VerificationToken token = verificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

            validateToken(token);

            token.getUser().setEnabled(true);
            token.setStatus(VerificationToken.STATUS_VERIFIED);
            token.setConfirmedDateTime(LocalDateTime.now());
            
            userRepository.save(token.getUser());
            
            return ResponseEntity.ok("Email verified successfully. You may now log in.");
        } catch (Exception e) {
            logger.error("Email verification failed", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void validateToken(VerificationToken token) {
        if (token.getStatus().equals(VerificationToken.STATUS_VERIFIED)) {
            throw new IllegalStateException("Email already verified");
        }

        if (token.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            throw new IllegalStateException("Token expired. Please request a new verification email.");
        }
    }

    private String generateVerificationUrl(HttpServletRequest request, String token){
        String baseUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
        return baseUrl + "/verify-email?token=" + token;
    }
    
    public String getEmailFromToken(String tokenValue) {
        return verificationTokenRepository.findByToken(tokenValue)
            .map(token -> token.getUser().getEmail())
            .orElse(null);
    }
}
