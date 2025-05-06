package com.group20.dailyreadingtracker.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller handling email verification workflows including:
 * - Processing verification token clicks
 * - Displaying verification status pages
 * - Managing verification email resends
 * 
 * @author Sofiia Mamonova
 */

@Controller
public class VerificationController {
    private final VerificationTokenService verificationTokenService;
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    public VerificationController(VerificationTokenService verificationTokenService){
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model, 
                              RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<String> response = verificationTokenService.verifyEmail(token);

            if (response.getStatusCode() == HttpStatus.OK) {
                return "redirect:/auth?verified=true";
            } else {
                String email = verificationTokenService.getEmailFromToken(token);
                redirectAttributes.addAttribute("error", response.getBody());

                if (email != null) {
                    redirectAttributes.addAttribute("email", email);
                }

                return "redirect:/verification-error";
            }
        } catch (Exception e) {
            logger.error("Email verification failed", e);
            redirectAttributes.addAttribute("error", e.getMessage());
            
            try {
                String email = verificationTokenService.getEmailFromToken(token);

                if (email != null) {
                    redirectAttributes.addAttribute("email", email);
                }
            } catch (Exception ex) {
                logger.warn("Could not extract email from token", ex);
            }
            
            return "redirect:/verification-error";
        }
    }

    @GetMapping("/verification-error")
    public String showVerificationError(@RequestParam(required = false) String error, 
                                        @RequestParam(required = false) String email, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("email", email);

        return "auth/verification-error";
    }

    @GetMapping("/verify-pending")
    public String showVerifyPending(@RequestParam String email,
                                    @RequestParam(required = false) Boolean resent, Model model) {
        model.addAttribute("email", email);

        if (Boolean.TRUE.equals(resent)) {
            model.addAttribute("message", "New verification email sent!");
        }

        return "auth/verify-pending";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, 
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            verificationTokenService.createVerificationForRegisteredUser(email, request);
            redirectAttributes.addAttribute("email", email);
            return "redirect:/verify-pending?resent=true";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Failed to resend verification: " + e.getMessage());
            redirectAttributes.addAttribute("email", email);
            return "redirect:/verification-error";
        }
    }
}
