package com.group20.dailyreadingtracker.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import com.group20.dailyreadingtracker.security.JWTTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;
import com.group20.dailyreadingtracker.utils.EmailService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Mock
    private EmailService emailService;

    @Mock
    private JWTTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private VerificationTokenService verificationTokenService;

    private User testUser;
    private VerificationToken validToken;
    private VerificationToken expiredToken;
    private VerificationToken verifiedToken;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("testuser@test.com");
        testUser.setEnabled(false);

        validToken = new VerificationToken();
        validToken.setToken("validToken");
        validToken.setUser(testUser);
        validToken.setStatus(VerificationToken.STATUS_PENDING);
        validToken.setExpiredDateTime(LocalDateTime.now().plusHours(24));

        expiredToken = new VerificationToken();
        expiredToken.setToken("expiredToken");
        expiredToken.setUser(testUser);
        expiredToken.setStatus(VerificationToken.STATUS_PENDING);
        expiredToken.setExpiredDateTime(LocalDateTime.now().minusHours(1));

        verifiedToken = new VerificationToken();
        verifiedToken.setToken("verifiedToken");
        verifiedToken.setUser(testUser);
        verifiedToken.setStatus(VerificationToken.STATUS_VERIFIED);
        verifiedToken.setExpiredDateTime(LocalDateTime.now().plusHours(24));
    }

    // VS_001
    @Test
    public void testCreateVerificationNewUser() {
        when(userRepository.findByEmail("usertest@test.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenUtil.generateToken("usertest@test.com")).thenReturn("generatedToken");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        when(request.getServletPath()).thenReturn("");

        VerificationToken expectedToken = new VerificationToken();
        expectedToken.setToken("generatedToken");
        when(tokenRepository.save(any())).thenReturn(expectedToken);

        verificationTokenService.createVerificationForRegisteredUser("usertest@test.com", request);

        verify(tokenRepository).deleteAllByUser(testUser);
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(eq(testUser), contains("generatedToken"));
    }

    // VS_002
    @Test
    public void testVerifyEmailValidToken() {
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(validToken));

        ResponseEntity<String> response = verificationTokenService.verifyEmail("validToken");

        assertTrue(testUser.isEnabled());
        assertEquals(VerificationToken.STATUS_VERIFIED, validToken.getStatus());
        assertNotNull(validToken.getConfirmedDateTime());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email verified successfully. You may now log in.", response.getBody());
    }

    // VS_003
    @Test
    public void testVerifyEmailAlreadyVerified() {
        when(tokenRepository.findByToken("verifiedToken")).thenReturn(Optional.of(verifiedToken));

        ResponseEntity<String> response = verificationTokenService.verifyEmail("verifiedToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Email already verified"));
        verify(userRepository, never()).save(any());
    }

    // VS_004
    @Test
    public void testVerifyEmailExpiredToken() {
        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(expiredToken));

        ResponseEntity<String> response = verificationTokenService.verifyEmail("expiredToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Token expired"));
        verify(tokenRepository).delete(expiredToken);
    }

    // VS_005
    @Test
    public void testVerifyEmailInvalidToken() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        ResponseEntity<String> response = verificationTokenService.verifyEmail("invalidToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid verification token"));
    }

    // VS_006
    @Test
    public void testGetEmailFromTokenValidToken() {
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(validToken));

        String email = verificationTokenService.getEmailFromToken("validToken");

        assertEquals("testuser@test.com", email);
    }

    // VS_007
    @Test
    void getEmailFromTokenInvalidToken() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        String email = verificationTokenService.getEmailFromToken("invalidToken");

        assertNull(email);
    }

}
