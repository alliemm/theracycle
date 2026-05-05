package com.theracycle.exceptions;

// This exception is thrown when a user tries to register with an already existing userID
public class DuplicateUserException extends Exception {

    private final String userId;

    public DuplicateUserException(String userId) {
        super("A user with ID '" + userId + "' is already registered.");
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
