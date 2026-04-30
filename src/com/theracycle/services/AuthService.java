package com.theracycle.services;

import com.theracycle.models.User;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    // Key: UserID, Value: User object (can be Patient or Therapist)
    private Map<String, User> userDatabase;

    public AuthService() {
        this.userDatabase = new HashMap<>();
    }

    // Signup logic
    public void registerUser(User user) {
        if (userDatabase.containsKey(user.getId())) {
            System.out.println("Error: User ID already exists.");
        } else {
            userDatabase.put(user.getId(), user);
            System.out.println("Registration successful for: " + user.getName());
        }
    }

    // Login logic
    public User login(String id) {
        if (userDatabase.containsKey(id)) {
            System.out.println("Login successful! Welcome back.");
            return userDatabase.get(id);
        }
        System.out.println("Login failed: ID not found.");
        return null;
    }
    public User getUserById(String id) {
        return userDatabase.get(id); // Just returns the user silently
    }
}