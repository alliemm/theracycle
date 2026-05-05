package com.theracycle.models;

/**
 * Abstract base class for all system users
 * Encapsulates shared identity fields (name, id) and enforces
 * polymorphic dashboard behavior via the abstract displayDashboard() method
 * Patient and Therapist both extend User — demonstrating inheritance
 */
public abstract class User {
    private final String name;
    private final String id;
    public User(String name, String id) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be blank.");
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID cannot be blank.");
        this.name = name.trim();
        this.id = id.trim();
    }
    public String getName() { return name; }
    public String getId()   { return id; }
    //Polymorphism: each subclass renders its own dashboard view
    public abstract void displayDashboard();
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "] " + name;
    }
}
