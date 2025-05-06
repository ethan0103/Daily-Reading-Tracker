package com.group20.dailyreadingtracker.app;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Controller handling main application pages and general error handling.
 * 
* Provides endpoints for:
 * - Primary application home page
 * - Global error handling and display
 * - CSRF token propagation to views
 * 
 * Main application endpoints:
 * - "/home" for the application home page
 * - "/error" for the error display page
 * 
 * @author Sofiia Mamonova
 */

@Controller
@RequiredArgsConstructor
public class AppController {

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model, HttpServletRequest request) {
        model.addAttribute("_csrf", request.getAttribute("_csrf"));
        return "readinglog/home";
    }

    @GetMapping("/error")
    public String showErrorPage() {
        return "error";
    }

}
