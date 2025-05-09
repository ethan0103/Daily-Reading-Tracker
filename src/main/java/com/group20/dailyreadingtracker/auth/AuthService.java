package com.group20.dailyreadingtracker.auth;

import java.io.IOException;
import java.util.HashSet;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service implementation for authentication and user registration operations.
 * 
 * This service handles:
 * - User registration with password encryption and validation
 * - Avatar image processing and storage
 * - Role assignment (admin for first user, regular user for others)
 * - Email verification workflow
 * 
 * @author Sofiia Mamonova
 */

@Service
public class AuthService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final FileStorageService fileStorageService;
    private final VerificationTokenService verificationTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                     BCryptPasswordEncoder encoder, FileStorageService fileStorageService, 
                     VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.fileStorageService = fileStorageService;
        this.verificationTokenService = verificationTokenService;
    }

    @Transactional
    public void register(User user, MultipartFile avatar, HttpServletRequest request){
        try {
            if (userRepository.existsByEmail(user.getEmail()) || 
            userRepository.existsByUsername(user.getUsername())) 
                throw new IllegalArgumentException("User already exists");

            if (!user.isPasswordsMatch())
                throw new RegistrationException("Passwords must match");
   
            user.setConfirmPassword(null);
            user.setPassword(encoder.encode(user.getPassword()));

            if (avatar != null && !avatar.isEmpty()) {
                try {
                    String avatarFilename = fileStorageService.storeAvatar(avatar, user.getUsername());
                    user.setAvatarFileName(avatarFilename);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store avatar", e);
                }
            }

            boolean isFirstUser = userRepository.count() == 0;

            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }

            Role defaultRole;
            if (isFirstUser) {
                defaultRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role("ROLE_ADMIN");
                        return roleRepository.save(newRole);
                    });
            } else {
                defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role("ROLE_USER");
                        return roleRepository.save(newRole);
                    });
            }

            user.getRoles().clear();
            user.getRoles().add(defaultRole);

            userRepository.save(user);

            if (request != null){
                try{
                    verificationTokenService.createVerificationForRegisteredUser(user.getEmail(), request);
                } catch (Exception e){
                    userRepository.delete(user);
                    throw new EmailVerificationException("Failed to send verification email", e);
                }
            }
        } catch (RuntimeException e) {
            throw new RegistrationException("Registration failed: " + e.getMessage(), e);
        }            
    }

    public class RegistrationException extends RuntimeException{
        public RegistrationException(String message, Throwable cause) {
            super(message, cause);
        }

        public RegistrationException(String message){
            super(message);
        }
    }

    public class EmailVerificationException extends RuntimeException {
        public EmailVerificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
