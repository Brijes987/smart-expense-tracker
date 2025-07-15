package com.expensetracker.budgetservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    /**
     * Extracts user ID from authentication object
     * In a real implementation, this would typically involve:
     * 1. Calling the User Service to get user details by username
     * 2. Or storing user ID in JWT claims
     * 
     * For this demo, we'll simulate by parsing username as ID
     */
    public Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        
        // In a real implementation, you would:
        // 1. Call User Service API to get user details
        // 2. Or decode user ID from JWT token claims
        // 3. Or maintain a user cache
        
        // For demo purposes, we'll assume username is numeric or use a hash
        try {
            return Long.parseLong(username);
        } catch (NumberFormatException e) {
            // If username is not numeric, use hash code as user ID
            // This is just for demo - in production, implement proper user resolution
            return Math.abs((long) username.hashCode());
        }
    }
    
    /**
     * Validates if the user has access to the resource
     * This could include role-based checks, ownership validation, etc.
     */
    public boolean hasAccessToResource(Authentication authentication, Long resourceUserId) {
        Long currentUserId = getUserIdFromAuthentication(authentication);
        return currentUserId.equals(resourceUserId);
    }
}