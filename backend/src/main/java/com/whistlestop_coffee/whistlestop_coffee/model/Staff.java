package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents an administrative user for the dashboard.
 * Note: Plaintext passwords are used strictly for this academic prototype.
 * A production environment would require BCrypt hashing and Spring Security integration.
 */
@Entity
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
    private String password;

    public Staff() {}

    public Staff(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Staff(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}