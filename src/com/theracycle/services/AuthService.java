package com.theracycle.services;

import com.theracycle.exceptions.DuplicateUserException;
import com.theracycle.models.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthService manages user registration and authentication
 * Uses a HashMap keyed by user ID — O(1) lookup for login
 * Throws DuplicateUserException on duplicate registration
 */
public class AuthService {
    private final Map<String, User> userDatabase;
    public AuthService() {
        this.userDatabase = new HashMap<>();
    }
    /**
     * Registers a new user
     * @param user the user to register
     * @throws DuplicateUserException if the ID is already taken
     */
    public void registerUser(User user) throws DuplicateUserException {
        if (userDatabase.containsKey(user.getId())) {
            throw new DuplicateUserException(user.getId());
        }
        userDatabase.put(user.getId(), user);
        System.out.println("Registered: " + user);
    }
    /**
     * Authenticates a user by ID
     * @param id the user ID to look up
     * @return the matching User, or null if not found
     */
    public User login(String id) {
        if (id == null || id.isBlank()) return null;
        User user = userDatabase.get(id.trim());
        if (user != null) {
            System.out.println("Login successful. Welcome, " + user.getName() + ".");
        } else {
            System.out.println("Access Denied: No account found for ID '" + id + "'.");
        }
        return user;
    }
    //Silent lookup — used internally
    public User getUserById(String id) {
        return userDatabase.get(id);
    }
    // Returns an unmodifiable view of all registered users.
    public Map<String, User> getAllUsers() {
        return Collections.unmodifiableMap(userDatabase);
    }
    public int getUserCount() {
        return userDatabase.size();
    }
}
