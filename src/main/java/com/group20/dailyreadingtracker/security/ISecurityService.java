package com.group20.dailyreadingtracker.security;

/**
 * Interface defining security-related operations
 * 
 * @author Sofiia Mamonova
 */

public interface ISecurityService {
    
    boolean isAuthenticated();

    void autoLogin(String username, String password);
}
