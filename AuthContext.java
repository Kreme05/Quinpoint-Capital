package com.quinpoint;

/**
 * Simple singleton for holding the logged-in user details.
 * This avoids passing user information around manually.
 */
public class AuthContext {
    private static AuthContext instance;

    private String userId;
    private String userName;
    private String userEmail;

    private AuthContext() {
        // private constructor to enforce singleton pattern
    }

    public static AuthContext getInstance() {
        if (instance == null) {
            instance = new AuthContext();
        }
        return instance;
    }

    // called after login
    public void login(String id, String name, String email) {
        this.userId = id;
        this.userName = name;
        this.userEmail = email;
    }

    // clear details on logout
    public void logout() {
        this.userId = null;
        this.userName = null;
        this.userEmail = null;
    }

    public boolean isAuthenticated() {
        return userId != null;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
