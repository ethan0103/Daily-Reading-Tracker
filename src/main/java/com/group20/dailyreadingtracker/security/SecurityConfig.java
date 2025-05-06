package com.group20.dailyreadingtracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.group20.dailyreadingtracker.auth.AccountFrozenException;

/**
 * Spring Security configuration class defining application security policies.
 * 
 * Configures:
 * - Authentication and authorization rules
 * - Password encoding strategy
 * - Login/logout behavior
 * - Exception handling for authentication failures
 * - Role-based access control
 * 
 * Security Features:
 * - BCrypt password hashing
 * - Form-based authentication
 * - Session management
 * - Role-based authorization
 * 
 * Authentication Flow:
 * - Custom success handler redirects based on user role
 * - Custom failure handler with specific error cases
 * - Email verification requirements
 * - Account frozen state handling
 * 
 * Endpoint Protection:
 * - Public endpoints for auth/registration
 * - Admin-only endpoints
 * - Authenticated user endpoints
 * 
 * @author Sofiia Mamonova
 * @author Yaxiang Yao
 * @author Yongtai Li
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") 
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/register",
                    "/register/**",
                    "/auth",
                    "/auth/**",
                    "/uploads/**",
                    "/verify-email",
                    "/verify-email/**",
                    "/verify-email**",
                    "/resend-verification",
                    "/resend-verification/**",
                    "/verify-pending",
                    "/verify-pending/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/forgot-password",
                    "/forgot-password/**",
                    "/reset-password/**",
                    "/reset-password",
                    "/logout",
                    "/account-frozen",
                    "/error"
                ).permitAll()
                .requestMatchers("/home").hasRole("USER") 
                .requestMatchers("/progress").hasRole("USER") 
                .requestMatchers("/profile").hasRole("USER") 
                .requestMatchers("/homeforadmin").hasRole("ADMIN")
                .requestMatchers("/alluserlogs").hasRole("ADMIN")
                .requestMatchers("/viologs").hasRole("ADMIN")
                .requestMatchers("/admin-profile").hasRole("ADMIN")
                .requestMatchers("/getviologs").hasRole("ADMIN")
                .requestMatchers("/sorted_admin_userlist").hasRole("ADMIN")
                .requestMatchers("/sorted_admin_userlist/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth")
                .failureHandler(authenticationFailureHandler())
                .loginProcessingUrl("/login") 
                .successHandler(authenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            
            .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth"))
        );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                System.out.println("SercurityConfig Login Success - Permissions: " + authentication.getAuthorities());
            String targetUrl = isAdmin ? "/homeforadmin" : "/home";
            response.sendRedirect(targetUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String email = request.getParameter("username");
            String redirectUrl = "/auth?error=true";
            
           if (exception instanceof InternalAuthenticationServiceException && 
            exception.getCause() instanceof CustomUserDetailsService.UnverifiedAccountException) {
            redirectUrl = "/verify-pending?email=" + email;
            }
            else if (exception instanceof BadCredentialsException) {
                redirectUrl = "/auth?error=true";
            }
            else if (exception instanceof InternalAuthenticationServiceException){
                Throwable cause = exception.getCause();
                if (cause instanceof AccountFrozenException) {
                    redirectUrl = "/account-frozen";
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }
}