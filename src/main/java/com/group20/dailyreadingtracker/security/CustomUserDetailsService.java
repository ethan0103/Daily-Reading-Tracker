package com.group20.dailyreadingtracker.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.auth.AccountFrozenException;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

/**
 * Custom implementation of Spring Security's UserDetailsService for loading user-specific data.
 * 
 * This service:
 * - Loads user details by email for authentication
 * - Checks if user account is verified before allowing login
 * - Converts application User roles to Spring Security GrantedAuthority objects
 * 
 * @author Sofiia Mamonova
 */

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isEnabled())
            throw new UnverifiedAccountException();

        if (user.isFreezed() == true) {
            throw new AccountFrozenException("Account is frozen"); 
        }
    
        Set<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), user.isEnabled(), 
            true,         true, true, authorities);
    }

    public class UnverifiedAccountException extends AuthenticationException {
        public UnverifiedAccountException() {
            super("Account not verified");
        }
    }
    
}

